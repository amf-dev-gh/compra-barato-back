package site.amf_dev.api_gateway.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SearchResponse {
	
	private boolean success;
	private String query;
	private int totalProducts;
	private LocalDateTime scrapDate = LocalDateTime.now();
	private List<ProductDto> products;

}
