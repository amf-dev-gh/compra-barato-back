package com.andres.whatsapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.andres.whatsapp.model.WhatsAppRequest;
import com.andres.whatsapp.service.WhatsAppService;

@RestController
@RequestMapping("/api/v1/whatsapp")
public class WhatsappController {

	@Autowired
	WhatsAppService waService;

	@PostMapping("/send")
	public boolean sendListMessage(@RequestBody WhatsAppRequest request) {
		boolean send = waService.sendMessage(request.getUsername(), request.getProducts(), request.getPhoneNumber());
		return send;
	}

}
