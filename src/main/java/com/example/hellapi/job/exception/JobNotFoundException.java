package com.example.hellapi.job.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JobNotFoundException extends RuntimeException {

	private final Long id;

	public JobNotFoundException(Long id) {
		super("Job not found: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}

