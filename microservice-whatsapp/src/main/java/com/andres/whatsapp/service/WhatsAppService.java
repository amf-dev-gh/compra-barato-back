package com.andres.whatsapp.service;

import java.util.List;

import com.andres.whatsapp.model.Product;

public interface WhatsAppService {

	public boolean sendMessage(String username, List<Product> products, String phoneNumber);

}