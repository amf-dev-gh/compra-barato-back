package com.andres.scrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceScrapConsumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceScrapConsumApplication.class, args);
	}
}
