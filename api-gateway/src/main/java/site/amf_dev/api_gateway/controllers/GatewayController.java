package site.amf_dev.api_gateway.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import site.amf_dev.api_gateway.dtos.ProductDto;
import site.amf_dev.api_gateway.dtos.ScrapResponse;
import site.amf_dev.api_gateway.service.ScraperService;

@RestController
@RequestMapping("/api/gateway")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GatewayController {

	private final ScraperService scraperService;

	@GetMapping("/search")
	public ResponseEntity<ScrapResponse> searchAllProducts(
			@RequestParam @NotBlank(message = "La consulta de búsqueda no puede estar vacía") String query,
			@RequestParam(defaultValue = "1") @Min(1) @Max(10) int pages) {
		List<ProductDto> products = new ArrayList<>();
		ScrapResponse response = new ScrapResponse();
		response.setSuccess(false);
		response.setQuery(query);

		try {
			CompletableFuture<List<ProductDto>> future = scraperService.startAllScraps(query, pages);
			products = future.get(60, TimeUnit.SECONDS);
			response.setProducts(products);
			response.setTotalProducts(products.size());
			response.setSuccess(true);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.setTotalProducts(0);
			response.setProducts(products);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

}
