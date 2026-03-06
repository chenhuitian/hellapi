package com.example.hellapi.scheduletrade;

import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import com.example.hellapi.singaporestock.moormoo.MoormooStockClient;
import com.example.hellapi.singaporestock.repository.SingaporeStockOrderRepository;
import com.example.hellapi.singaporestock.service.SingaporeStockService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutoSellScheduleServiceTest {

	@Mock
	private SingaporeStockOrderRepository orderRepository;

	@Mock
	private SingaporeStockService singaporeStockService;

	@Mock
	private MoormooStockClient stockClient;

	@InjectMocks
	private AutoSellScheduleService service;

	@Test
	void checkAndAutoSell_whenPriceBelowThreshold_doesNotSell() {
		ReflectionTestUtils.setField(service, "thresholdPercent", 10);
		SingaporeStockOrder buyOrder = createBuyOrder("D05", new BigDecimal("100.00"), 100);
		when(orderRepository.findBuyOrdersNotAutoSold()).thenReturn(List.of(buyOrder));
		when(stockClient.getQuote("D05")).thenReturn(java.util.Optional.of(new BigDecimal("105.00")));

		service.checkAndAutoSell();

		verify(singaporeStockService, never()).placeAutoSellOrder(any(), any());
	}

	@Test
	void checkAndAutoSell_whenPriceAtThreshold_sells() {
		ReflectionTestUtils.setField(service, "thresholdPercent", 10);
		SingaporeStockOrder buyOrder = createBuyOrder("D05", new BigDecimal("100.00"), 100);
		when(orderRepository.findBuyOrdersNotAutoSold()).thenReturn(List.of(buyOrder));
		when(stockClient.getQuote("D05")).thenReturn(java.util.Optional.of(new BigDecimal("110.00")));

		service.checkAndAutoSell();

		verify(singaporeStockService).placeAutoSellOrder(eq(buyOrder), eq(new BigDecimal("110.00")));
	}

	@Test
	void checkAndAutoSell_whenNoQuote_skips() {
		SingaporeStockOrder buyOrder = createBuyOrder("D05", new BigDecimal("100.00"), 100);
		when(orderRepository.findBuyOrdersNotAutoSold()).thenReturn(List.of(buyOrder));
		when(stockClient.getQuote("D05")).thenReturn(java.util.Optional.empty());

		service.checkAndAutoSell();

		verify(singaporeStockService, never()).placeAutoSellOrder(any(), any());
	}

	private static SingaporeStockOrder createBuyOrder(String symbol, BigDecimal price, int qty) {
		SingaporeStockOrder order = new SingaporeStockOrder();
		order.setId(1L);
		order.setSymbol(symbol);
		order.setSide(SingaporeStockOrder.OrderSide.BUY);
		order.setQuantity(qty);
		order.setPrice(price);
		order.setStatus(SingaporeStockOrder.OrderStatus.SUBMITTED);
		order.setAutoSold(false);
		return order;
	}
}
