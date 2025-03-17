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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.andres.scrap.model.Product;
import com.andres.scrap.service.ScrapService;

import io.github.bonigarcia.wdm.WebDriverManager;

@Service
public class ScrapServiceImpl implements ScrapService {

	private static final Logger logger = LoggerFactory.getLogger(ScrapServiceImpl.class);

	private static String url_Consum = "https://tienda.consum.es/es";
	
	private static String provider_name = "Consum";

	@Override
	public List<Product> findProducts(String value) {
		LocalTime init = LocalTime.now();
		List<Product> products = new ArrayList<>();
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver(getChromeOptions());
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		try {
			driver.get(url_Consum + "/s/" + value);
			try {
				WebElement btnCookies = wait
						.until(ExpectedConditions.elementToBeClickable(By.id("onetrust-reject-all-handler")));
				btnCookies.click();
				waitFor("CARGAR PÁGINA DESPUES DE COOKIES.");
				boolean hasNextPage;
				int pageCount = 1;
				do {
					List<WebElement> weProducts = driver.findElements(By.className("widget-prod"));
					for (WebElement p : weProducts) {
						try {
							String name = p.findElement(By.id("grid-widget--descr")).getText();
							String unitPrice = p.findElement(By.id("grid-widget--price")).getText();
							String measurementUnit = p.findElement(By.className("widget-prod__info-unitprice")).getText();
							Product product = new Product(name, unitPrice, measurementUnit, "-", provider_name);
							products.add(product);
						} catch (Exception e) {
							logger.warn("----------> ERROR AL AGREGAR PRODUCTO", e);
						}
					}
					try {
						WebElement btnNext = wait
								.until(ExpectedConditions.presenceOfElementLocated(By.className("next-page")));
						hasNextPage = btnNext.isDisplayed() && btnNext.isEnabled();
						if (hasNextPage) {
							btnNext.click();
							pageCount++;
							waitFor("SIGUIENTE PÁGINA");
						}
					} catch (Exception e) {
						hasNextPage = false;
					}
				} while (hasNextPage && pageCount <= 6);

			} catch (Exception e) {
				logger.warn("----------> NO SE ENCONTRO BOTON DE COOKIES", e);
			}
		} catch (Exception e) {
			logger.info("---------> ERROR AL CARGAR LA WEB", e);
		} finally {
			driver.quit();
		}
		timeElapsedFor("BUSCAR PRODUCTOS", init);
		return products;
	}

	// ----------> METODOS INTERNOS

	private ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless");
		return options;
	}
	
	private void waitFor(String doing) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("----------> HILO INTERRUMPIDO MIENTRAS SE ESPERABA " + doing);
		}
	}
	
	private void timeElapsedFor(String doing, LocalTime init) {
		LocalTime end = LocalTime.now();
		Duration duration = Duration.between(init, end);
		logger.info("----------> TIEMPO TRANSCURRIDO: " + duration.toSeconds() + " SEGUNDOS.");
		logger.info("----------> FIN DE " + doing);
	}

}
