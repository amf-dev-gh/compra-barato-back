package com.andres.scrap.service.impl;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.andres.scrap.model.Product;
import com.andres.scrap.service.ScrapService;
import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class ScrapServiceImpl implements ScrapService {

	// https://tienda.mercadona.es/search-results?query=limon

	private static final Logger logger = LoggerFactory.getLogger(ScrapServiceImpl.class);

	private static String url_mercadona = "https://tienda.mercadona.es/";

	private static String provider_name = "Mercadona";

	@Override
	public List<Product> findProducts(String value) {
		LocalTime init = LocalTime.now();
		List<Product> products = new ArrayList<>();
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(getChromeOptions());
		try {
			driver.get(url_mercadona + "search-results?query=" + value);
			waitFor("CARGAR LA WEB DE MERCADONA");

			try {
				List<WebElement> weProducts = driver.findElements(By.className("product-cell"));

				for (WebElement p : weProducts) {

					try {
						List<WebElement> el = p.findElements(By.className("footnote1-r"));
						String measurementUnit = el.get(0).getText() + " " + el.get(1).getText();
						String name = p.findElement(By.className("product-cell__description-name")).getText();
						String unitPrice = p.findElement(By.className("product-price__unit-price")).getText();
						String unit = p.findElement(By.className("product-price__extra-price")).getText();
						Product product = new Product(name, unitPrice, unit, measurementUnit, provider_name);
						products.add(product);
					} catch (Exception e) {
						logger.info("----------> ERROR AL OBTENER UN PRODUCTO", e);
					}

				}

			} catch (Exception e) {
				logger.info("----------> NO SE ENCONTRARON PRODUCTOS", e);
			}

		} catch (Exception e) {
			logger.info("----------> NO SE PUDO ACCEDER A LA WEB DE MERCADONA", e);
		} finally {
			driver.quit();
		}
		timeElapsedFor("BUSCAR PRODUCTOS", init);
		return products;
	}

	// ----------> Metodos internos del servicio

	private ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		return options;
	}

	private void timeElapsedFor(String doing, LocalTime init) {
		LocalTime end = LocalTime.now();
		Duration duration = Duration.between(init, end);
		logger.info("----------> TIEMPO TRANSCURRIDO: " + duration.toSeconds() + " SEGUNDOS.");
		logger.info("----------> FIN DE " + doing);
	}

	private void waitFor(String doing) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("----------> HILO INTERRUMPIDO MIENTRAS SE ESPERABA " + doing);
		}
	}
}
