package com.example.hellapi.singaporestock.moormoo;

import java.math.BigDecimal;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delegates to Moomoo/Futu OpenAPI for fetching Singapore stock quotes.
 * Uses reflection to avoid hard dependency on specific API version.
 */
class MoormooQuoteDelegate {

	private static final Logger log = LoggerFactory.getLogger(MoormooQuoteDelegate.class);

	private final String host;
	private final int port;

	MoormooQuoteDelegate(String host, int port) {
		this.host = host;
		this.port = port;
	}

	Optional<BigDecimal> getLastPrice(String symbol) {
		try {
			Object quoteCtx = openQuoteContext();
			if (quoteCtx == null) {
				return Optional.empty();
			}
			try {
				return fetchQuote(quoteCtx, symbol);
			} finally {
				closeContext(quoteCtx);
			}
		} catch (Exception e) {
			log.debug("Moomoo quote error for {}: {}", symbol, e.getMessage());
			return Optional.empty();
		}
	}

	private Object openQuoteContext() {
		try {
			// Futu/Moomoo: OpenQuoteContext or QuoteContext
			for (String className : new String[] {
				"com.futu.openapi.ftapi.OpenQuoteContext",
				"com.futu.openapi.ftapi.QuoteContext"
			}) {
				try {
					Class<?> ctxClass = Class.forName(className);
					Object ctx = ctxClass.getConstructor(String.class, int.class)
						.newInstance(host, port);
					java.lang.reflect.Method init = ctxClass.getMethod("init");
					Boolean ok = (Boolean) init.invoke(ctx);
					if (Boolean.TRUE.equals(ok)) {
						return ctx;
					}
				} catch (ClassNotFoundException | NoSuchMethodException ignored) {
					// try next
				}
			}
		} catch (Exception e) {
			log.trace("QuoteContext init failed: {}", e.getMessage());
		}
		return null;
	}

	private Optional<BigDecimal> fetchQuote(Object ctx, String symbol) {
		try {
			// Subscribe then get quote, or try getBasicQuote / getSecuritySnapshot directly
			for (java.lang.reflect.Method m : ctx.getClass().getMethods()) {
				String name = m.getName();
				if (("getBasicQuote".equals(name) || "getSecuritySnapshot".equals(name))
					&& m.getParameterCount() >= 1) {
					try {
						Object result = m.getParameterCount() == 1
							? m.invoke(ctx, java.util.Collections.singletonList(symbol))
							: m.invoke(ctx, new Object[] { java.util.Collections.singletonList(symbol), 6 });
						return parseQuoteResult(result, symbol);
					} catch (Exception ignored) {
						// try next
					}
				}
			}
		} catch (Exception e) {
			log.trace("Fetch quote failed: {}", e.getMessage());
		}
		return Optional.empty();
	}

	private Optional<BigDecimal> parseQuoteResult(Object result, String symbol) {
		if (result == null) {
			return Optional.empty();
		}
		try {
			// Common pattern: retType, data/list with lastPrice or last_price
			java.lang.reflect.Method getRetType = result.getClass().getMethod("getRetType");
			Integer retType = (Integer) getRetType.invoke(result);
			if (retType != null && retType != 0) {
				return Optional.empty();
			}
			// Get data - could be List<BasicQot> or similar
			Object data = null;
			for (String getter : new String[] { "getBasicQotList", "getSnapshotList", "getData" }) {
				try {
					java.lang.reflect.Method getData = result.getClass().getMethod(getter);
					data = getData.invoke(result);
					break;
				} catch (NoSuchMethodException ignored) {
				}
			}
			if (data instanceof java.util.List && !((java.util.List<?>) data).isEmpty()) {
				Object item = ((java.util.List<?>) data).get(0);
				for (String priceField : new String[] { "getLastPrice", "getLast_price", "getCurPrice" }) {
					try {
						java.lang.reflect.Method getPrice = item.getClass().getMethod(priceField);
						Object price = getPrice.invoke(item);
						if (price instanceof Number) {
							return Optional.of(BigDecimal.valueOf(((Number) price).doubleValue()));
						}
					} catch (NoSuchMethodException ignored) {
					}
				}
			}
		} catch (Exception e) {
			log.trace("Parse quote failed: {}", e.getMessage());
		}
		return Optional.empty();
	}

	private void closeContext(Object ctx) {
		try {
			if (ctx != null) {
				ctx.getClass().getMethod("close").invoke(ctx);
			}
		} catch (Exception ignored) {
		}
	}
}
