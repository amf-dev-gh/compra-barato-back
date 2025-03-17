package com.andres.scrap.service;

import java.util.List;

import com.andres.scrap.model.Product;

public interface ScrapService {
	
	public List<Product> findProducts(String value);

}
