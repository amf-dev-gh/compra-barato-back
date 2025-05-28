package site.amf_dev.consum.dtos;

import lombok.Data;

@Data
public class ProductDto {

	private String name;
	private String price;
	private String imageUrl;
	private String description;
    private String store;

}
