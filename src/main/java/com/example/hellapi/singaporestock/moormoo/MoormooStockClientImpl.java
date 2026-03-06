package com.example.hellapi.singaporestock.moormoo;

import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Moomoo OpenAPI integration for Singapore stock trading.
 * Connects to OpenD gateway. Set moomoo.enabled=true and ensure OpenD is running.
 */
@Component
@ConditionalOnProperty(name = "moomoo.enabled", havingValue = "true")
public class MoormooStockClientImpl implements MoormooStockClient {

	private static final Logger log = LoggerFactory.getLogger(MoormooStockClientImpl.class);

	@Value("${moomoo.host:127.0.0.1}")
	private String host;

	@Value("${moomoo.port:11111}")
	private int port;

	@Value("${moomoo.paper-trading:true}")
	private boolean paperTrading;

	@Override
	public PlaceOrderResult placeOrder(String symbol, boolean isBuy, int quantity, BigDecimal price,
		boolean isLimitOrder) {
		// Singapore stock code format: SG.D05 for DBS, SG.O39 for OCBC, etc.
		String sgSymbol = symbol.contains(".") ? symbol : "SG." + symbol;

		try {
			// Attempt Moomoo OpenAPI integration via TrdContext
			MoormooPlaceOrderDelegate delegate = new MoormooPlaceOrderDelegate(host, port, paperTrading);
			MoormooPlaceOrderDelegate.OrderResult result = delegate.placeOrder(sgSymbol, isBuy, quantity, price, isLimitOrder);

			if (result != null && result.isSuccess()) {
				return PlaceOrderResult.builder()
					.success(true)
					.externalOrderId(result.getOrderId())
					.message("Order submitted to Moomoo")
					.build();
			}
			return PlaceOrderResult.builder()
				.success(false)
				.message(result != null ? result.getMessage() : "OpenD connection failed. Ensure OpenD is running at " + host + ":" + port)
				.build();
		} catch (Exception e) {
			log.error("Moomoo place order failed: {}", e.getMessage());
			return PlaceOrderResult.builder()
				.success(false)
				.message("Moomoo API error: " + e.getMessage())
				.build();
		}
	}

	@Override
	public Optional<BigDecimal> getQuote(String symbol) {
		String sgSymbol = symbol.contains(".") ? symbol : "SG." + symbol;
		try {
			MoormooQuoteDelegate delegate = new MoormooQuoteDelegate(host, port);
			return delegate.getLastPrice(sgSymbol);
		} catch (Exception e) {
			log.debug("Get quote failed for {}: {}", symbol, e.getMessage());
			return Optional.empty();
		}
	}

	@Override
	public boolean isAvailable() {
		return true;
	}
}
