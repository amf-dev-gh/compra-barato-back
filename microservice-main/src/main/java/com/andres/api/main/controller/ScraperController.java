package com.andres.api.main.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andres.api.main.model.Product;
import com.andres.api.main.model.Store;
import com.andres.api.main.service.ProductService;

@RestController
@RequestMapping("/api/v1/scrap")
@CrossOrigin(origins = "http://localhost:4200")
public class ScraperController {

	@Autowired
	ProductService productService;

	/*
	 * Recibe un valor como parametro en la URL, envia la peticion a cada
	 * microservicio de scrap, aguarda ambas respuestas y las une para enviarlas
	 * como respuesta
	 * 
	 * @return ResponseEntity con los listados de productos encontrados.
	 */
	@GetMapping("/products/{value}")
	public CompletableFuture<ResponseEntity<List<Product>>> sttartConsumCategoriesScraping(@PathVariable String value) {
		CompletableFuture<List<Product>> productsC = productService.getConsumProducts(value);
		CompletableFuture<List<Product>> productsM = productService.getMercadonaProducts(value);
		return CompletableFuture.allOf(productsC, productsM).thenApply(v -> {
			List<Product> allProducts = new ArrayList<>();
			allProducts.addAll(productsC.join());
			allProducts.addAll(productsM.join());
			return new ResponseEntity<>(allProducts, HttpStatus.OK);
		});
	}

	/*
	 * @return Retorna listado de los stores disponibles para scrap
	 */
	@GetMapping("/stores")
	public ResponseEntity<List<Store>> getStores() {
		List<Store> stores = new ArrayList<>();
		stores.add(new Store("Mercadona", "https://www.google.com/maps/search/Mercadona+cerca+de+mi",
				"https://i.postimg.cc/BQCnSXNc/logo-mercadona.jpg"));
		stores.add(new Store("Consum", "https://www.google.com/maps/search/Consum+cerca+de+mi",
				"https://i.postimg.cc/TYxPvrj3/logo-consum.jpg"));
		return new ResponseEntity<>(stores, HttpStatus.OK);
	}

}
