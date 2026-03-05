package com.example.hellapi.trade.dto;

import com.example.hellapi.trade.entity.Trade;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeResponse {

	private Long id;
	private String name;
	private BigDecimal price;
	private String description;

	public static TradeResponse from(Trade trade) {
		return TradeResponse.builder()
			.id(trade.getId())
			.name(trade.getName())
			.price(trade.getPrice())
			.description(trade.getDescription())
			.build();
	}
}
