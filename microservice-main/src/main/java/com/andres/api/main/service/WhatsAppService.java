package com.andres.api.main.service;

import org.springframework.stereotype.Service;

import com.andres.api.main.client.WhatsAppClient;
import com.andres.api.main.model.WhatsAppRequest;

@Service
public class WhatsAppService {
	
	private final WhatsAppClient waClientService;
	
	public WhatsAppService(WhatsAppClient waService) {
		this.waClientService = waService;
	}
	
	public boolean sendListMessage(WhatsAppRequest request) {
		return waClientService.sendMessage(request);
	}

}
