package com.andres.api.main.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product {

	private String name;
	private String unitPrice;
	private String unit;
	private String measurementUnit;
	private String provider;

	@Override
	public String toString() {
		return "Product [name=" + name + ", unitPrice=" + unitPrice + ", unit=" + unit + ", measurementUnit="
				+ measurementUnit + ", store=" + provider + "]";
	}
}