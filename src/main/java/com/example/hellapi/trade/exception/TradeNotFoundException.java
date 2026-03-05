package com.example.hellapi.trade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TradeNotFoundException extends RuntimeException {

	private final Long id;

	public TradeNotFoundException(Long id) {
		super("Trade not found: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
