package com.example.hellapi.singaporestock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SingaporeStockOrderNotFoundException extends RuntimeException {

	private final Long id;

	public SingaporeStockOrderNotFoundException(Long id) {
		super("Singapore stock order not found: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
