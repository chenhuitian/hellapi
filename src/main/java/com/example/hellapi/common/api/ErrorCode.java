package com.example.hellapi.common.api;

public enum ErrorCode {
	PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found"),
	PRODUCT_NOT_DELETED("PRODUCT_NOT_DELETED", "Product is not deleted"),
	JOB_NOT_FOUND("JOB_NOT_FOUND", "Job not found"),
	JOB_NOT_DELETED("JOB_NOT_DELETED", "Job is not deleted"),
	TRADE_NOT_FOUND("TRADE_NOT_FOUND", "Trade not found"),
	TRADE_NOT_DELETED("TRADE_NOT_DELETED", "Trade is not deleted"),
	SINGAPORE_STOCK_ORDER_NOT_FOUND("SINGAPORE_STOCK_ORDER_NOT_FOUND", "Singapore stock order not found"),
	VALIDATION_ERROR("VALIDATION_ERROR", "Validation failed"),
	CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "Constraint violation"),
	INVALID_BODY("INVALID_BODY", "Invalid request body"),
	FORBIDDEN("FORBIDDEN", "Forbidden"),
	UNAUTHORIZED("UNAUTHORIZED", "Unauthorized"),
	ROLE_NOT_FOUND("ROLE_NOT_FOUND", "Role not found"),
	ROLE_NAME_EXISTS("ROLE_NAME_EXISTS", "Role name already exists"),
	INTERNAL_ERROR("INTERNAL_ERROR", "Unexpected error");

	private final String code;
	private final String defaultMessage;

	ErrorCode(String code, String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	public String getCode() {
		return code;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}
}
