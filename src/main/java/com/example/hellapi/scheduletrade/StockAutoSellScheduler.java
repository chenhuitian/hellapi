package com.example.hellapi.scheduletrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler that checks stock prices every 5 minutes and auto-sells when
 * price is 10% above bought price.
 */
@Component
@ConditionalOnProperty(name = "schedule-trade.enabled", havingValue = "true", matchIfMissing = false)
public class StockAutoSellScheduler {

	private static final Logger log = LoggerFactory.getLogger(StockAutoSellScheduler.class);

	private final AutoSellScheduleService autoSellScheduleService;

	public StockAutoSellScheduler(AutoSellScheduleService autoSellScheduleService) {
		this.autoSellScheduleService = autoSellScheduleService;
	}

	@Scheduled(fixedRateString = "${schedule-trade.interval-ms:300000}")
	public void runAutoSellCheck() {
		log.debug("Running auto-sell price check");
		autoSellScheduleService.checkAndAutoSell();
	}
}
