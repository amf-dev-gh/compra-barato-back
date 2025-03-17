package com.andres.api.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andres.api.main.model.WhatsAppRequest;
import com.andres.api.main.service.WhatsAppService;

@RestController
@RequestMapping("/api/v1/whatsapp")
@CrossOrigin(origins = "http://localhost:4200")
public class WhatsAppController {
	
	@Autowired
	WhatsAppService waService;
	
	@PostMapping("/send")
	public ResponseEntity<Boolean> sendWhatsapMessage(@RequestBody WhatsAppRequest request){
		boolean send = waService.sendListMessage(request);
		return new ResponseEntity<>(send, HttpStatus.OK);
	}

}
