package com.andres.api.main.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppRequest {
	
	private List<Product> products;
	private String username;
	private String phoneNumber;

}
