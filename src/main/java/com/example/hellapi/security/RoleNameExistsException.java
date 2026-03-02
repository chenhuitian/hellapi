package com.example.hellapi.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoleNameExistsException extends RuntimeException {

	public RoleNameExistsException(String name) {
		super("Role already exists: " + name);
	}
}
