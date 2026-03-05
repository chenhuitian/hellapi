package com.example.hellapi.singaporestock.service;

import com.example.hellapi.singaporestock.dto.PlaceOrderRequest;
import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import com.example.hellapi.singaporestock.exception.SingaporeStockOrderNotFoundException;
import com.example.hellapi.singaporestock.moormoo.MoormooStockClient;
import com.example.hellapi.singaporestock.repository.SingaporeStockOrderRepository;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SingaporeStockService {

	private final SingaporeStockOrderRepository orderRepository;
	private final MoormooStockClient moormooClient;

	public SingaporeStockService(SingaporeStockOrderRepository orderRepository,
		MoormooStockClient moormooClient) {
		this.orderRepository = orderRepository;
		this.moormooClient = moormooClient;
	}

	@Transactional
	public SingaporeStockOrder placeOrder(PlaceOrderRequest request) {
		SingaporeStockOrder order = SingaporeStockOrder.builder()
			.symbol(request.getSymbol())
			.side(request.getSide())
			.quantity(request.getQuantity())
			.price(request.getPrice())
			.orderType(request.getOrderType())
			.status(SingaporeStockOrder.OrderStatus.PENDING)
			.deleted(false)
			.createdAt(Instant.now())
			.build();
		order = orderRepository.save(order);

		// Submit to Moomoo OpenAPI when available
		MoormooStockClient.PlaceOrderResult result = moormooClient.placeOrder(
			request.getSymbol(),
			request.getSide() == SingaporeStockOrder.OrderSide.BUY,
			request.getQuantity(),
			request.getPrice(),
			request.getOrderType() == SingaporeStockOrder.OrderType.LIMIT
		);

		if (result.isSuccess()) {
			order.setStatus(SingaporeStockOrder.OrderStatus.SUBMITTED);
			order.setExternalOrderId(result.getExternalOrderId());
			order.setMessage(result.getMessage());
		} else {
			order.setStatus(SingaporeStockOrder.OrderStatus.FAILED);
			order.setMessage(result.getMessage());
		}
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public Page<SingaporeStockOrder> getAll(Pageable pageable, boolean includeDeleted) {
		if (includeDeleted) {
			return orderRepository.findAllIncludeDeleted(pageable);
		}
		return orderRepository.findAllByDeletedFalse(pageable);
	}

	@Transactional(readOnly = true)
	public SingaporeStockOrder getById(Long id, boolean includeDeleted) {
		if (includeDeleted) {
			return orderRepository.findByIdIncludeDeleted(id)
				.orElseThrow(() -> new SingaporeStockOrderNotFoundException(id));
		}
		return orderRepository.findByIdAndDeletedFalse(id)
			.orElseThrow(() -> new SingaporeStockOrderNotFoundException(id));
	}

	@Transactional
	public void delete(Long id) {
		if (!orderRepository.existsByIdAndDeletedFalse(id)) {
			throw new SingaporeStockOrderNotFoundException(id);
		}
		orderRepository.deleteById(id);
	}

	public boolean isMoormooAvailable() {
		return moormooClient.isAvailable();
	}
}
