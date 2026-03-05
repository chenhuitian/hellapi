package com.example.hellapi.common.error;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.common.api.ErrorCode;
import java.util.Locale;
import com.example.hellapi.product.exception.ProductNotDeletedException;
import com.example.hellapi.product.exception.ProductNotFoundException;
import com.example.hellapi.trade.exception.TradeNotDeletedException;
import com.example.hellapi.trade.exception.TradeNotFoundException;
import com.example.hellapi.singaporestock.exception.SingaporeStockOrderNotFoundException;
import com.example.hellapi.security.RoleNameExistsException;
import com.example.hellapi.security.RoleNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final MessageSource messageSource;

	public GlobalExceptionHandler(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(ProductNotFoundException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.PRODUCT_NOT_FOUND, new Object[] { ex.getId() }, ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, ErrorCode.PRODUCT_NOT_FOUND, message, request, null);
	}

	@ExceptionHandler(ProductNotDeletedException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotDeleted(ProductNotDeletedException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.PRODUCT_NOT_DELETED, new Object[] { ex.getId() }, ex.getMessage());
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.PRODUCT_NOT_DELETED, message, request, null);
	}

	@ExceptionHandler(TradeNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleTradeNotFound(TradeNotFoundException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.TRADE_NOT_FOUND, new Object[] { ex.getId() }, ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, ErrorCode.TRADE_NOT_FOUND, message, request, null);
	}

	@ExceptionHandler(TradeNotDeletedException.class)
	public ResponseEntity<ApiResponse<Void>> handleTradeNotDeleted(TradeNotDeletedException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.TRADE_NOT_DELETED, new Object[] { ex.getId() }, ex.getMessage());
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.TRADE_NOT_DELETED, message, request, null);
	}

	@ExceptionHandler(SingaporeStockOrderNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleSingaporeStockOrderNotFound(SingaporeStockOrderNotFoundException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.SINGAPORE_STOCK_ORDER_NOT_FOUND, new Object[] { ex.getId() }, ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, ErrorCode.SINGAPORE_STOCK_ORDER_NOT_FOUND, message, request, null);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.FORBIDDEN, null, "Forbidden");
		return buildError(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, message, request, null);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.UNAUTHORIZED, null, "Unauthorized");
		return buildError(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, message, request, null);
	}

	@ExceptionHandler(RoleNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleRoleNotFound(RoleNotFoundException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.ROLE_NOT_FOUND, null, ex.getMessage());
		return buildError(HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND, message, request, null);
	}

	@ExceptionHandler(RoleNameExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleRoleNameExists(RoleNameExistsException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.ROLE_NAME_EXISTS, null, ex.getMessage());
		return buildError(HttpStatus.CONFLICT, ErrorCode.ROLE_NAME_EXISTS, message, request, null);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		String message = resolveMessage(ErrorCode.VALIDATION_ERROR, null, "Validation failed");
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, message, request, fieldErrors);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.CONSTRAINT_VIOLATION, null, ex.getMessage());
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.CONSTRAINT_VIOLATION, message, request, null);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.INVALID_BODY, null, "Invalid request body");
		return buildError(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_BODY, message, request, null);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
		String message = resolveMessage(ErrorCode.INTERNAL_ERROR, null, "Unexpected error");
		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, message, request, null);
	}

	private ResponseEntity<ApiResponse<Void>> buildError(HttpStatus status, ErrorCode code, String message,
		HttpServletRequest request, Map<String, String> fieldErrors) {
		ApiResponse<Void> error = ApiResponse.error(status.value(), code, message, request.getRequestURI(), fieldErrors);
		return ResponseEntity.status(status).body(error);
	}

	private String resolveMessage(ErrorCode code, Object[] args, String fallback) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage("error." + code.getCode(), args, fallback, locale);
	}
}
