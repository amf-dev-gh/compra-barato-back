package com.andres.api.main.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.andres.api.main.model.Product;

@FeignClient(name = "microservice-scrap-mercadona")
public interface ScrapingClientMercadona {
	
	 @GetMapping("/api/v1/scrap/products/{value}")
	public List<Product> getProducts(@PathVariable String value);

}
