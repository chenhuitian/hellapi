package com.example.hellapi.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

	@NotBlank(message = "{product.name.notBlank}")
	@Size(max = 255, message = "{product.name.size}")
	private String name;

	@NotNull(message = "{product.price.notNull}")
	@DecimalMin(value = "0.0", inclusive = false, message = "{product.price.min}")
	private BigDecimal price;

	@Size(max = 1000, message = "{product.description.size}")
	private String description;
}
