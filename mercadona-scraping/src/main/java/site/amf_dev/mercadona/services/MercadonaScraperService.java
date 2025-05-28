package site.amf_dev.mercadona.services;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;

import site.amf_dev.mercadona.dtos.ProductDto;
import site.amf_dev.mercadona.exceptions.ScrapingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class MercadonaScraperService {

	private static final Logger logger = LoggerFactory.getLogger(MercadonaScraperService.class);

	@Value("${playwright.headless}")
	private boolean headless;

	@Value("${playwright.timeout}")
	private int timeout;

	@Value("${playwright.user-agent}")
	private String userAgent;

	@Value("${playwright.viewport.width}")
	private int viewportWidth;

	@Value("${playwright.viewport.height}")
	private int viewportHeight;

	@Value("${playwright.delay.min}")
	private int delayMin;

	@Value("${playwright.delay.max}")
	private int delayMax;

	@Value("${scraper.max-retries}")
	private int maxRetries;

	@Value("${scraper.wait-for-selector-timeout}")
	private int waitForSelectorTimeout;

	@Value("${scraper.cookie-acceptance-timeout}")
	private int cookieAcceptanceTimeout;

	private final Random random = new Random();

	public List<ProductDto> searchProducts(String query) {
		return searchProducts(query, 1);
	}

	public List<ProductDto> searchProducts(String query, int maxPages) {
		List<ProductDto> products = new ArrayList<>();

		try (Playwright playwright = Playwright.create()) {
			Browser browser = createBrowser(playwright);
			BrowserContext context = createBrowserContext(browser);
			Page page = context.newPage();

			try {
				// Configurar la página
				configurePage(page);

				// Navegar a Mercadona y buscar productos
				navigateToPage(page, query);
				acceptCookies(page);

				// Extraer productos de múltiples páginas
				for (int pageNum = 1; pageNum <= maxPages; pageNum++) {
					logger.info("Extrayendo productos de la página {}", pageNum);

					List<ProductDto> pageProducts = extractProductsFromCurrentPage(page);
					products.addAll(pageProducts);

					if (pageNum < maxPages && hasNextPage(page)) {
						goToNextPage(page);
						humanLikeDelay();
					} else {
						break;
					}
				}

			} finally {
				context.close();
				browser.close();
			}
		} catch (Exception e) {
			logger.error("Error durante el scraping", e);
			throw new ScrapingException("Error al realizar scraping en Mercadona", e);
		}

		logger.info("Se extrajeron {} productos en total", products.size());
		return products;
	}

	private Browser createBrowser(Playwright playwright) {
		BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless)
				.setTimeout(timeout)
				.setArgs(List.of("--no-sandbox", "--disable-blink-features=AutomationControlled",
						"--disable-dev-shm-usage", "--disable-extensions", "--disable-plugins", "--disable-images",
						"--disable-javascript", "--user-agent=" + userAgent));

		return playwright.chromium().launch(launchOptions);
	}

	private BrowserContext createBrowserContext(Browser browser) {
		Browser.NewContextOptions contextOptions = new Browser.NewContextOptions()
				.setViewportSize(viewportWidth, viewportHeight).setUserAgent(userAgent).setJavaScriptEnabled(true);

		return browser.newContext(contextOptions);
	}

	private void configurePage(Page page) {
		// Configurar timeout por defecto
		page.setDefaultTimeout(timeout);
//		Interceptar requests para optimizar carga
//		page.route("**/*.{png,jpg,jpeg,gif,svg,css,woff,woff2}", route -> route.abort());

		// Inyectar script para evitar detección
		page.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
	}

	private void navigateToPage(Page page, String query) {
		int retries = 0;
		while (retries < maxRetries) {
			try {
				logger.info("Navegando a Mercadona (intento {})", retries + 1);
				page.navigate("https://tienda.mercadona.es/search-results?query=" + query);
				page.waitForLoadState(LoadState.NETWORKIDLE);
				humanLikeDelay();
				return;
			} catch (Exception e) {
				retries++;
				logger.warn("Error al navegar a Mercadona, intento {}: {}", retries, e.getMessage());
				if (retries >= maxRetries) {
					throw new ScrapingException("No se pudo navegar a Mercadona después de " + maxRetries + " intentos",
							e);
				}
				humanLikeDelay();
			}
		}
	}

	private void acceptCookies(Page page) {
		try {
			logger.info("Intentando aceptar cookies");

			// Diferentes selectores posibles para el botón de cookies
			String[] cookieSelectors = { "#onetrust-accept-btn-handler", "[data-testid='cookie-accept-all']",
					"button[aria-label*='Aceptar']", "button:has-text('Aceptar todo')", "button:has-text('Acepto')",
					".cookie-banner button:first-child" };

			for (String selector : cookieSelectors) {
				try {
					if (page.locator(selector).isVisible()) {
						page.locator(selector).click();
						logger.info("Cookies aceptadas con selector: {}", selector);
						humanLikeDelay();
						return;
					}
				} catch (Exception e) {
					logger.debug("Selector de cookies no encontrado: {}", selector);
				}
			}

			logger.info("No se encontró banner de cookies o ya fue aceptado");
		} catch (Exception e) {
			logger.warn("Error al aceptar cookies: {}", e.getMessage());
		}
	}

	private List<ProductDto> extractProductsFromCurrentPage(Page page) {
		List<ProductDto> products = new ArrayList<>();

		try {
			// Esperar a que se carguen los productos
			page.waitForSelector(".product-card, .product-item, [data-testid*='product']",
					new Page.WaitForSelectorOptions().setTimeout(waitForSelectorTimeout));

			// Diferentes selectores posibles para productos
			String[] productSelectors = { ".product-card", ".product-cell", "[data-testid*='product-cell']",
					".product-container" };

			Locator productElements = null;
			for (String selector : productSelectors) {
				try {
					productElements = page.locator(selector);
					if (productElements.count() > 0) {
						logger.info("Encontrados productos con selector: {}", selector);
						break;
					}
				} catch (Exception e) {
					logger.debug("Selector de productos no encontrado: {}", selector);
				}
			}

			if (productElements == null || productElements.count() == 0) {
				logger.warn("No se encontraron productos en la página");
				return products;
			}

			int productCount = productElements.count();
			logger.info("Encontrados {} productos en la página", productCount);

			for (int i = 0; i < productCount; i++) {
				try {
					Locator productElement = productElements.nth(i);
					ProductDto product = extractProductInfo(productElement);
					if (product != null) {
						products.add(product);
					}
				} catch (Exception e) {
					logger.warn("Error al extraer producto {}: {}", i, e.getMessage());
				}
			}

		} catch (Exception e) {
			logger.error("Error al extraer productos de la página", e);
		}

		return products;
	}

	private ProductDto extractProductInfo(Locator productElement) {
		try {
			ProductDto product = new ProductDto();

			// Extraer nombre
			try {
				String name = extractText(productElement, ".product-title", ".product-name", "h3", "h4",
						"[data-testid*='product-cell-name']");
				product.setName(name);
			} catch (Exception e) {
				logger.debug("No se pudo extraer el nombre del producto");
			}

			// Extraer precio
			try {
				String price = extractText(productElement, ".price-current", ".price", "[data-testid*='product-price']",
						".product-price");
				product.setPrice(price);
			} catch (Exception e) {
				logger.debug("No se pudo extraer el precio del producto");
			}

			// Extraer imagen
			try {
				String imageUrl = extractImageUrl(productElement, ".product-image img", ".product-img img", "img");
				product.setImageUrl(imageUrl);
			} catch (Exception e) {
				logger.debug("No se pudo extraer la imagen del producto");
			}

			// Extraer URL del producto
			try {
				String productDescription = extractProductDescription(productElement, ".product-format");
				product.setDescription(productDescription);
			} catch (Exception e) {
				logger.debug("No se pudo extraer la URL del producto");
			}

			// Solo devolver el producto si tiene al menos nombre y precio
			if (product.getName() != null && product.getPrice() != null) {
				product.setStore("Mercadona");
				return product;
			}

		} catch (Exception e) {
			logger.warn("Error al extraer información del producto: {}", e.getMessage());
		}

		return null;
	}

	private String extractText(Locator productElement, String... selectors) {
		for (String selector : selectors) {
			try {
				Locator element = productElement.locator(selector);
				if (element.isVisible()) {
					return element.textContent().trim();
				}
			} catch (Exception e) {
				logger.debug("Selector de título no encontrado: {}", selector);
			}
		}
		return null;
	}

	private String extractProductDescription(Locator productElement, String... selectors) {
		for (String selector : selectors) {
			try {
				Locator element = productElement.locator(selector);
				if (element.isVisible()) {
					return element.textContent().trim();
				}
			} catch (Exception e) {
				logger.debug("Selector de descripción no encontrado: {}", selector);
			}
		}
		return null;
	}

	private String extractImageUrl(Locator productElement, String... selectors) {
		for (String selector : selectors) {
			try {
				Locator element = productElement.locator(selector);
				if (element.isVisible()) {
					String src = element.getAttribute("src");
					if (src != null && !src.isEmpty()) {
						return src.startsWith("http") ? src : "https://tienda.mercadona.es" + src;
					}
				}
			} catch (Exception e) {
				logger.debug("Selector de imagen no encontrado: {}", selector);
			}
		}
		return null;
	}

	private boolean hasNextPage(Page page) {
		try {
			String[] nextPageSelectors = { ".pagination-next:not(.disabled)",
					"[aria-label*='Siguiente']:not(.disabled)", "button:has-text('Siguiente'):not(.disabled)" };

			for (String selector : nextPageSelectors) {
				try {
					if (page.locator(selector).isVisible()) {
						return true;
					}
				} catch (Exception e) {
					logger.debug("Selector de página siguiente no encontrado: {}", selector);
				}
			}
			return false;
		} catch (Exception e) {
			logger.debug("Error al verificar página siguiente: {}", e.getMessage());
			return false;
		}
	}

	private void goToNextPage(Page page) {
		try {
			String[] nextPageSelectors = { ".pagination-next:not(.disabled)",
					"[aria-label*='Siguiente']:not(.disabled)", "button:has-text('Siguiente'):not(.disabled)" };

			for (String selector : nextPageSelectors) {
				try {
					Locator nextButton = page.locator(selector);
					if (nextButton.isVisible()) {
						nextButton.click();
						page.waitForLoadState(LoadState.NETWORKIDLE);
						humanLikeDelay();
						return;
					}
				} catch (Exception e) {
					logger.debug("No se pudo hacer clic en el botón siguiente: {}", selector);
				}
			}

			throw new ScrapingException("No se encontró el botón de página siguiente");
		} catch (Exception e) {
			throw new ScrapingException("Error al navegar a la siguiente página", e);
		}
	}

	private void humanLikeDelay() {
		try {
			int delay = random.nextInt(delayMax - delayMin) + delayMin;
			TimeUnit.MILLISECONDS.sleep(delay);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			logger.warn("Delay interrumpido", e);
		}
	}
}