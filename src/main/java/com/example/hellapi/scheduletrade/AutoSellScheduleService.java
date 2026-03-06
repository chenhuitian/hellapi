package com.example.hellapi.scheduletrade;

import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import com.example.hellapi.singaporestock.moormoo.MoormooStockClient;
import com.example.hellapi.singaporestock.repository.SingaporeStockOrderRepository;
import com.example.hellapi.singaporestock.service.SingaporeStockService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Checks Singapore stock positions and auto-sells when price rises 10% above bought price.
 */
@Service
public class AutoSellScheduleService {

	private static final Logger log = LoggerFactory.getLogger(AutoSellScheduleService.class);

	private final SingaporeStockOrderRepository orderRepository;
	private final SingaporeStockService singaporeStockService;
	private final MoormooStockClient moormooClient;

	@Value("${schedule-trade.auto-sell-threshold-percent:10}")
	private int thresholdPercent;

	public AutoSellScheduleService(SingaporeStockOrderRepository orderRepository,
		SingaporeStockService singaporeStockService,
		MoormooStockClient moormooClient) {
		this.orderRepository = orderRepository;
		this.singaporeStockService = singaporeStockService;
		this.moormooClient = moormooClient;
	}

	/**
	 * Run the auto-sell check: for each BUY position not yet auto-sold,
	 * fetch current price and sell if price >= bought * (1 + threshold/100).
	 */
	public void checkAndAutoSell() {
		List<SingaporeStockOrder> buyOrders = orderRepository.findBuyOrdersNotAutoSold();
		if (buyOrders.isEmpty()) {
			return;
		}
		for (SingaporeStockOrder buyOrder : buyOrders) {
			try {
				processBuyOrder(buyOrder);
			} catch (Exception e) {
				log.warn("Auto-sell check failed for order {} ({}): {}", buyOrder.getId(), buyOrder.getSymbol(), e.getMessage());
			}
		}
	}

	private void processBuyOrder(SingaporeStockOrder buyOrder) {
		BigDecimal boughtPrice = buyOrder.getPrice();
		BigDecimal currentPrice = moormooClient.getQuote(buyOrder.getSymbol()).orElse(null);
		if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
			log.debug("No quote for {}, skipping auto-sell", buyOrder.getSymbol());
			return;
		}
		BigDecimal thresholdMultiplier = BigDecimal.ONE.add(
			BigDecimal.valueOf(thresholdPercent).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
		BigDecimal targetPrice = boughtPrice.multiply(thresholdMultiplier);
		if (currentPrice.compareTo(targetPrice) >= 0) {
			log.info("Auto-selling {}: current {} >= target {} (bought {})",
				buyOrder.getSymbol(), currentPrice, targetPrice, boughtPrice);
			singaporeStockService.placeAutoSellOrder(buyOrder, currentPrice);
		}
	}
}
