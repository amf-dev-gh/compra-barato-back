package com.andres.whatsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceWhatsappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceWhatsappApplication.class, args);
	}

}
