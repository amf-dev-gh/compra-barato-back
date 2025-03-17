package com.andres.scrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MicroserviceScrapMercadonaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceScrapMercadonaApplication.class, args);
	}

// Ejecicion en orden al momento de iniciar la app
//	@Bean
//	@Transactional
//	@Order(value = 1)
//	CommandLineRunner initData(ScrapServiceImpl scrapService) {
//		return args -> {};
//	}
}