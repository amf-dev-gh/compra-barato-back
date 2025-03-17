package com.andres.whatsapp.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.andres.whatsapp.model.Product;
import com.andres.whatsapp.service.WhatsAppService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

	@Value("${whatsapp.account.sid}")
	private String ACCOUNT_SID;
	
	@Value("${whatsapp.auth.token}")
	private String AUTH_TOKEN;
	
	@Value("${whatsapp.company.phone.number}")
	private String COMPANY_PHONE_NUMBER;
	
	private static final Logger logger = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

	@Override
	public boolean sendMessage(String msj, List<Product> products, String phoneNumber) {
		try {
			Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
			Message message = Message.creator(
					//NUMERO REMITENTE (viene por parámetro proneNumber)
					new PhoneNumber("whatsapp:" + "+34697345197"),
					//NUMERO EMPRESA
					new PhoneNumber("whatsapp:" + COMPANY_PHONE_NUMBER),
					createMessage(msj, products)).create();
			logger.info("----------> MENSAJE ENVIADO", message.getSid());
		} catch (Exception e) {
			logger.info("----------> ERROR AL ENVIAR MENSAJE", e);
			return false;
		}
		return true;
	}

	public String createMessage(String username, List<Product> productos) {
		String name = capitalizeFirstLetter(username);
		Map<String, List<Product>> productosPorProveedor = productos.stream()
				.collect(Collectors.groupingBy(Product::getProvider));
		StringBuilder mensaje = new StringBuilder()
				.append("*¡Gracias ").append(name)
				.append(", por usar Compra Barato!* 🎉\n\n")
				.append("🛒 *Aquí está su lista de compras:* \n\n");
		mensaje.append(buildProductList(productosPorProveedor));
		mensaje.append("\n🔍 ¡Compare precios y ahorre en su próxima compra con nosotros! \n\n")
				.append("📲 Visite *Compra Barato* aquí: http://amf-dev.site \n\n")
				.append("¡Esperamos verlo pronto! 😊");
		return mensaje.toString();
	}

	private String capitalizeFirstLetter(String texto) {
		if (texto == null || texto.isEmpty()) {
			return texto;
		}
		return texto.substring(0, 1).toUpperCase() + texto.substring(1);
	}

	private String buildProductList(Map<String, List<Product>> productosPorProveedor) {
		StringBuilder lista = new StringBuilder();
		productosPorProveedor
				.forEach((proveedor, productos) -> {
					lista.append("-"+proveedor).append(":\n");
					productos.forEach(p -> 
						lista.append("✅ ").append(p.getName()).append(", ")
						.append(p.getUnitPrice()).append("\n"));
				});
		return lista.toString();
	}
}
