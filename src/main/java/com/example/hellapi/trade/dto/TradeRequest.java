package com.example.hellapi.trade.dto;

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
public class TradeRequest {

	@NotBlank(message = "{trade.name.notBlank}")
	@Size(max = 255, message = "{trade.name.size}")
	private String name;

	@NotNull(message = "{trade.price.notNull}")
	@DecimalMin(value = "0.0", inclusive = false, message = "{trade.price.min}")
	private BigDecimal price;

	@Size(max = 1000, message = "{trade.description.size}")
	private String description;
}
