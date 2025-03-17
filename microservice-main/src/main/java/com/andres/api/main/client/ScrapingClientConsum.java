package com.andres.api.main.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;

import com.andres.api.main.model.Product;

import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "microservice-scrap-consum")
public interface ScrapingClientConsum {
	
	 @GetMapping("/api/v1/scrap/products/{value}")
	public List<Product> getProducts(@PathVariable String value);

}