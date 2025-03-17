package com.andres.api.main.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.andres.api.main.client.ScrapingClientConsum;
import com.andres.api.main.client.ScrapingClientMercadona;
import com.andres.api.main.model.Product;

@Service
public class ProductService {

	private final ScrapingClientConsum scrapConsum;

	private final ScrapingClientMercadona scrapMercadona;

	public ProductService(ScrapingClientConsum scrapConsum, ScrapingClientMercadona scrapMercadona) {
		this.scrapConsum = scrapConsum;
		this.scrapMercadona = scrapMercadona;
	}

	@Async
	public CompletableFuture<List<Product>> getConsumProducts(String value) {
		return CompletableFuture.completedFuture(scrapConsum.getProducts(value));
	}

	@Async
	public CompletableFuture<List<Product>> getMercadonaProducts(String value) {
		return CompletableFuture.completedFuture(scrapMercadona.getProducts(value));
	}

}
