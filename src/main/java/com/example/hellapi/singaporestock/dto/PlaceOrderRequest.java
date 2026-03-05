package com.example.hellapi.singaporestock.dto;

import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class PlaceOrderRequest {

	@NotBlank(message = "{stock.symbol.notBlank}")
	@Size(max = 32, message = "{stock.symbol.size}")
	private String symbol;

	@NotNull(message = "{stock.side.notNull}")
	private SingaporeStockOrder.OrderSide side;

	@NotNull(message = "{stock.quantity.notNull}")
	@Min(value = 1, message = "{stock.quantity.min}")
	private Integer quantity;

	@NotNull(message = "{stock.price.notNull}")
	@DecimalMin(value = "0.0", inclusive = false, message = "{stock.price.min}")
	private BigDecimal price;

	@NotNull(message = "{stock.orderType.notNull}")
	private SingaporeStockOrder.OrderType orderType;
}
