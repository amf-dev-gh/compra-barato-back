package com.andres.scrap.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.andres.scrap.model.Product;
import com.andres.scrap.service.ScrapService;

@RestController
@RequestMapping("/api/v1/scrap")
public class ScrapController {

	@Autowired
	ScrapService scrapService;

	private static final Logger logger = LoggerFactory.getLogger(ScrapController.class);

	@GetMapping("/products/{value}")
	public List<Product> getProducts(@PathVariable String value) {
		logger.info("---------> BUSCANDO PRODUCTOS - REST CONTROLLER (microservice-scrap-consum)");
		List<Product> products = scrapService.findProducts(value);
		return products;
	}
}
