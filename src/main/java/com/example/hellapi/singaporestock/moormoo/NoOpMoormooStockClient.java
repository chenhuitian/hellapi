package com.example.hellapi.singaporestock.moormoo;

import java.math.BigDecimal;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * No-op client when Moomoo integration is disabled.
 * Orders are stored locally but not submitted to Moomoo OpenAPI.
 */
@Component
@ConditionalOnProperty(name = "moomoo.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpMoormooStockClient implements MoormooStockClient {

	@Override
	public PlaceOrderResult placeOrder(String symbol, boolean isBuy, int quantity, BigDecimal price,
		boolean isLimitOrder) {
		return PlaceOrderResult.builder()
			.success(true)
			.externalOrderId("LOCAL-" + System.currentTimeMillis())
			.message("Order stored locally. Enable moomoo.enabled=true and run OpenD to submit to Moomoo.")
			.build();
	}

	@Override
	public boolean isAvailable() {
		return false;
	}
}
