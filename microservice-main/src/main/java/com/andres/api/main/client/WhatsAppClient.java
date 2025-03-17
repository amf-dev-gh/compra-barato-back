package com.andres.api.main.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.andres.api.main.model.WhatsAppRequest;

@FeignClient(name = "microservice-whatsapp")
public interface WhatsAppClient {
	
	 @PostMapping("/api/v1/whatsapp/send")
	public boolean sendMessage(@RequestBody WhatsAppRequest request);

}
