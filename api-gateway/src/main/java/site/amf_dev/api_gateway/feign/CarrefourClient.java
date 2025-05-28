package site.amf_dev.api_gateway.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import site.amf_dev.api_gateway.dtos.ProductDto;

@FeignClient(name = "carrefour-scraping")
public interface CarrefourClient {

	@GetMapping("/api/carrefour/search")
	ResponseEntity<List<ProductDto>> startScrap(
			@RequestParam @NotBlank(message = "La consulta de búsqueda no puede estar vacía") String query,
			@RequestParam(defaultValue = "1") @Min(1) @Max(10) int pages);

}
