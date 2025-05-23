package site.amf_dev.carrefour.dtos;

import lombok.Data;

@Data
public class ProductDto {

	private String name;
	private String price;
	private String imageUrl;
	private String description;
	private String store;

}
