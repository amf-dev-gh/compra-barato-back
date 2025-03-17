package com.andres.api.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class MicroserviceMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceMainApplication.class, args);
	}

}
