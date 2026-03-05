package com.example.hellapi.trade.service;

import com.example.hellapi.trade.dto.TradeRequest;
import com.example.hellapi.trade.entity.Trade;
import com.example.hellapi.trade.exception.TradeNotDeletedException;
import com.example.hellapi.trade.exception.TradeNotFoundException;
import com.example.hellapi.trade.repository.TradeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeService {

	private final TradeRepository tradeRepository;

	public TradeService(TradeRepository tradeRepository) {
		this.tradeRepository = tradeRepository;
	}

	@Transactional
	public Trade create(TradeRequest request) {
		Trade trade = Trade.builder()
			.name(request.getName())
			.price(request.getPrice())
			.description(request.getDescription())
			.deleted(false)
			.build();
		return tradeRepository.save(trade);
	}

	@Transactional(readOnly = true)
	public Page<Trade> getAll(Pageable pageable, boolean includeDeleted) {
		if (includeDeleted) {
			return tradeRepository.findAllIncludeDeleted(pageable);
		}
		return tradeRepository.findAllByDeletedFalse(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Trade> getDeleted(Pageable pageable) {
		return tradeRepository.findAllDeleted(pageable);
	}

	@Transactional(readOnly = true)
	public Trade getById(Long id, boolean includeDeleted) {
		if (includeDeleted) {
			return tradeRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new TradeNotFoundException(id));
		}
		return tradeRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new TradeNotFoundException(id));
	}

	@Transactional
	public Trade update(Long id, TradeRequest request) {
		Trade trade = tradeRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new TradeNotFoundException(id));
		trade.setName(request.getName());
		trade.setPrice(request.getPrice());
		trade.setDescription(request.getDescription());
		return tradeRepository.save(trade);
	}

	@Transactional
	public void delete(Long id) {
		if (!tradeRepository.existsByIdAndDeletedFalse(id)) {
			throw new TradeNotFoundException(id);
		}
		tradeRepository.deleteById(id);
	}

	@Transactional
	public Trade restore(Long id) {
		Trade trade = tradeRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new TradeNotFoundException(id));
		if (!trade.isDeleted()) {
			throw new TradeNotDeletedException(id);
		}
		trade.setDeleted(false);
		trade.setDeletedAt(null);
		return tradeRepository.save(trade);
	}
}
