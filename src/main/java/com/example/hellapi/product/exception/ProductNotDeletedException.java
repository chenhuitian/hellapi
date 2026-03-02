package com.example.hellapi.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductNotDeletedException extends RuntimeException {

	private final Long id;

	public ProductNotDeletedException(Long id) {
		super("Product is not deleted: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
