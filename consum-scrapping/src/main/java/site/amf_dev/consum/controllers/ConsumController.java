package site.amf_dev.consum.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import site.amf_dev.consum.dtos.ProductDto;
import site.amf_dev.consum.exceptions.ScrapingException;
import site.amf_dev.consum.services.ConsumScraperService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consum")
@CrossOrigin(origins = "*")
public class ConsumController {

	private static final Logger logger = LoggerFactory.getLogger(ConsumController.class);

	@Autowired
	private ConsumScraperService scraperService;

	@GetMapping("/search")
	public ResponseEntity<?> searchProducts(
			@RequestParam @NotBlank(message = "La consulta de búsqueda no puede estar vacía") String query,
			@RequestParam(defaultValue = "1") @Min(1) @Max(10) int pages) {

		try {
			logger.info("Iniciando búsqueda de productos para: '{}' en {} página(s)", query, pages);

			List<ProductDto> products = scraperService.searchProducts(query, pages);

			return ResponseEntity.ok(products);

		} catch (ScrapingException e) {
			logger.error("Error de scraping: {}", e.getMessage(), e);
			return createErrorResponse("Error durante el scraping", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			logger.error("Error inesperado: {}", e.getMessage(), e);
			return createErrorResponse("Error inesperado", "Ha ocurrido un error interno",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchProductsByPath(@PathVariable @NotBlank String query,
			@RequestParam(defaultValue = "1") @Min(1) @Max(10) int pages) {

		return searchProducts(query, pages);
	}

	@GetMapping("/health")
	public ResponseEntity<Map<String, Object>> healthCheck() {
		Map<String, Object> health = new HashMap<>();
		health.put("status", "UP");
		health.put("service", "Consum Scraper");
		health.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.ok(health);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> handleValidationException(IllegalArgumentException e) {
		logger.warn("Error de validación: {}", e.getMessage());
		return createErrorResponse("Error de validación", e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ScrapingException.class)
	public ResponseEntity<?> handleScrapingException(ScrapingException e) {
		logger.error("Error de scraping: {}", e.getMessage(), e);
		return createErrorResponse("Error de scraping", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGenericException(Exception e) {
		logger.error("Error inesperado: {}", e.getMessage(), e);
		return createErrorResponse("Error interno", "Ha ocurrido un error inesperado",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Map<String, Object>> createErrorResponse(String error, String message, HttpStatus status) {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("error", error);
		response.put("message", message);
		response.put("timestamp", System.currentTimeMillis());

		return ResponseEntity.status(status).body(response);
	}
}