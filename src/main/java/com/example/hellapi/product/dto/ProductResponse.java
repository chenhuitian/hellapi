package com.example.hellapi.product.dto;

import com.example.hellapi.product.entity.Product;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

	private Long id;
	private String name;
	private BigDecimal price;
	private String description;

	public static ProductResponse from(Product product) {
		return ProductResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.description(product.getDescription())
			.build();
	}
}
