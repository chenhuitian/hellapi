package com.example.hellapi.singaporestock.dto;

import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingaporeStockOrderResponse {

	private Long id;
	private String symbol;
	private SingaporeStockOrder.OrderSide side;
	private Integer quantity;
	private BigDecimal price;
	private SingaporeStockOrder.OrderType orderType;
	private SingaporeStockOrder.OrderStatus status;
	private String externalOrderId;
	private String message;
	private Instant createdAt;

	public static SingaporeStockOrderResponse from(SingaporeStockOrder order) {
		return SingaporeStockOrderResponse.builder()
			.id(order.getId())
			.symbol(order.getSymbol())
			.side(order.getSide())
			.quantity(order.getQuantity())
			.price(order.getPrice())
			.orderType(order.getOrderType())
			.status(order.getStatus())
			.externalOrderId(order.getExternalOrderId())
			.message(order.getMessage())
			.createdAt(order.getCreatedAt())
			.build();
	}
}
