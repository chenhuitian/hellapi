package com.example.hellapi.job.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JobNotDeletedException extends RuntimeException {

	private final Long id;

	public JobNotDeletedException(Long id) {
		super("Job is not deleted: " + id);
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
