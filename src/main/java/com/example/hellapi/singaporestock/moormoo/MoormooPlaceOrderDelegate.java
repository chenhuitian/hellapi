package com.example.hellapi.singaporestock.moormoo;

import java.math.BigDecimal;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates to Moomoo/Futu OpenAPI for placing Singapore stock orders.
 * Uses reflection to avoid hard dependency on specific API version.
 */
class MoormooPlaceOrderDelegate {

	private static final Logger log = LoggerFactory.getLogger(MoormooPlaceOrderDelegate.class);

	private final String host;
	private final int port;
	private final boolean paperTrading;

	MoormooPlaceOrderDelegate(String host, int port, boolean paperTrading) {
		this.host = host;
		this.port = port;
		this.paperTrading = paperTrading;
	}

	OrderResult placeOrder(String symbol, boolean isBuy, int quantity, BigDecimal price, boolean isLimitOrder) {
		try {
			Object trdCtx = openSecTradeContext();
			if (trdCtx == null) {
				return new OrderResult(false, null,
					"OpenD not available. Install OpenD (https://www.moomoo.com/OpenAPI), login with Moomoo SG account, and ensure it runs at " + host + ":" + port);
			}

			try {
				Object placeResult = callPlaceOrder(trdCtx, symbol, isBuy, quantity, price.doubleValue(), isLimitOrder);
				return parsePlaceOrderResult(placeResult);
			} finally {
				closeContext(trdCtx);
			}
		} catch (Exception e) {
			log.debug("Moomoo API error: {}", e.getMessage());
			return new OrderResult(false, null, "Moomoo API: " + e.getMessage());
		}
	}

	private Object openSecTradeContext() {
		try {
			// moomoo-api / futu-api: TrdContext subclasses
			Class<?> contextClass = Class.forName("com.futu.openapi.ftapi.TrdContext");
			Object ctx = contextClass.getConstructor(String.class, int.class, boolean.class)
				.newInstance(host, port, false);
			java.lang.reflect.Method init = contextClass.getMethod("init");
			Boolean ok = (Boolean) init.invoke(ctx);
			return Boolean.TRUE.equals(ok) ? ctx : null;
		} catch (Exception e) {
			log.trace("TrdContext init failed: {}", e.getMessage());
			return null;
		}
	}

	private Object callPlaceOrder(Object ctx, String symbol, boolean isBuy, int quantity, double price,
		boolean isLimitOrder) throws Exception {
		// Moomoo/Futu PlaceOrder - API varies by version. Try common signatures.
		Class<?> ctxClass = ctx.getClass();
		// TrdMarket.SG = 6, TrdSide.Buy = 1, Sell = 2
		int trdMarket = 6;
		int trdSide = isBuy ? 1 : 2;

		for (java.lang.reflect.Method m : ctxClass.getMethods()) {
			if ("placeOrder".equals(m.getName()) && m.getParameterCount() >= 5) {
				Class<?>[] params = m.getParameterTypes();
				try {
					// Common: (code, trdMarket, trdSide, qty, price, ...)
					if (params.length >= 6 && params[0] == String.class) {
						return m.invoke(ctx, symbol, trdMarket, trdSide, quantity, price, paperTrading ? 1 : 0);
					}
					if (params.length >= 5) {
						return m.invoke(ctx, symbol, trdMarket, trdSide, quantity, price);
					}
				} catch (Exception ignored) {
					// Try next overload
				}
			}
		}
		throw new UnsupportedOperationException("PlaceOrder method not found");
	}

	private OrderResult parsePlaceOrderResult(Object result) {
		if (result == null) {
			return new OrderResult(false, null, "No response from PlaceOrder");
		}
		try {
			// Result typically has retType, orderId, etc.
			java.lang.reflect.Method getRetType = result.getClass().getMethod("getRetType");
			Integer retType = (Integer) getRetType.invoke(result);
			if (retType != null && retType == 0) {
				String orderId = null;
				try {
					java.lang.reflect.Method getOrderId = result.getClass().getMethod("getOrderId");
					orderId = String.valueOf(getOrderId.invoke(result));
				} catch (Exception ignored) {
				}
				return new OrderResult(true, orderId, "OK");
			}
			java.lang.reflect.Method getRetMsg = result.getClass().getMethod("getRetMsg");
			String msg = (String) getRetMsg.invoke(result);
			return new OrderResult(false, null, msg != null ? msg : "Place order failed");
		} catch (Exception e) {
			return new OrderResult(false, null, "Parse error: " + e.getMessage());
		}
	}

	private void closeContext(Object ctx) {
		try {
			if (ctx != null) {
				java.lang.reflect.Method close = ctx.getClass().getMethod("close");
				close.invoke(ctx);
			}
		} catch (Exception ignored) {
		}
	}

	@Value
	static class OrderResult {
		boolean success;
		String orderId;
		String message;
	}
}
