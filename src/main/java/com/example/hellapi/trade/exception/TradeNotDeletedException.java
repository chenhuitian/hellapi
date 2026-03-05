package com.example.hellapi.trade.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TradeNotDeletedException extends RuntimeException {

	private final Long id;

	public TradeNotDeletedException(Long id) {
		super("Trade is not deleted: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
