package site.amf_dev.api_gateway.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.amf_dev.api_gateway.dtos.ProductDto;
import site.amf_dev.api_gateway.feign.CarrefourClient;
import site.amf_dev.api_gateway.feign.ConsumClient;
import site.amf_dev.api_gateway.feign.MercadonaClient;

@Service
@RequiredArgsConstructor
public class ScraperService {

	private static final Logger logger = LoggerFactory.getLogger(ScraperService.class);

	private final MercadonaClient mercadonaClient;
	private final CarrefourClient carrefourClient;
	private final ConsumClient consumClient;

	public CompletableFuture<List<ProductDto>> startAllScraps(String query, int pages) {
		List<ProductDto> foundProducts = new ArrayList<>();

		// Scrap de Mercadona
		CompletableFuture<List<ProductDto>> mercadonaFuture = CompletableFuture.supplyAsync(() -> {
			try {
				List<ProductDto> products = mercadonaClient.startScrap(query, pages).getBody();

				return products != null ? products : new ArrayList<>();
			} catch (Exception e) {
				logger.error("Error al obtener productos de Mercadona", e.getMessage());
				return new ArrayList<>();
			}
		});

		// scrap de consum
		CompletableFuture<List<ProductDto>> consumFuture = CompletableFuture.supplyAsync(() -> {
			try {
				List<ProductDto> products = consumClient.startScrap(query, pages).getBody();

				return products != null ? products : new ArrayList<>();
			} catch (Exception e) {
				logger.error("Error al obtener productos de Consum", e.getMessage());
				return new ArrayList<>();
			}
		});

		// scrap de carrefour
		CompletableFuture<List<ProductDto>> carrefourFuture = CompletableFuture.supplyAsync(() -> {
			try {
				List<ProductDto> products = carrefourClient.startScrap(query, pages).getBody();

				return products != null ? products : new ArrayList<>();
			} catch (Exception e) {
				logger.error("Error al obtener productos de Carrefour", e.getMessage());
				return new ArrayList<>();
			}
		});

		// Esperar a que todos terminen y retornar respuesta completa
		return CompletableFuture.allOf(mercadonaFuture).thenApply(v -> {
			try {
				List<ProductDto> mercadonaProducts = mercadonaFuture.get();
				List<ProductDto> consumProducts = consumFuture.get();
				List<ProductDto> carrefourProducts = carrefourFuture.get();

				foundProducts.addAll(consumProducts);
				foundProducts.addAll(mercadonaProducts);
				foundProducts.addAll(carrefourProducts);

				return foundProducts;
			} catch (Exception e) {
				throw new RuntimeException("Error al unir productos: " + e.getMessage());
			}
		});
	}

}
