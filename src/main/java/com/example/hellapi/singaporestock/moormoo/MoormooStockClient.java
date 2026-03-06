package com.example.hellapi.singaporestock.moormoo;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.Builder;
import lombok.Value;

/**
 * Client for Moomoo OpenAPI integration to buy/sell Singapore stocks.
 * Requires OpenD gateway running (default: 127.0.0.1:11111).
 * @see <a href="https://openapi.moomoo.com/moomoo-api-doc/">Moomoo API Documentation</a>
 */
public interface MoormooStockClient {

	PlaceOrderResult placeOrder(String symbol, boolean isBuy, int quantity, BigDecimal price,
		boolean isLimitOrder);

	/**
	 * Get current last price for a symbol. Returns empty when unavailable (e.g. Moomoo disabled).
	 */
	Optional<BigDecimal> getQuote(String symbol);

	boolean isAvailable();

	@Value
	@Builder
	class PlaceOrderResult {
		boolean success;
		String externalOrderId;
		String message;
	}
}
