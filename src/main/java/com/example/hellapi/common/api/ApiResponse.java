package com.example.hellapi.common.api;

import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

	private boolean success;
	private String code;
	private String message;
	private int status;
	private T data;
	private Instant timestamp;
	private String path;
	private Map<String, String> errors;

	public static <T> ApiResponse<T> ok(T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.code("OK")
			.message("OK")
			.status(200)
			.data(data)
			.timestamp(Instant.now())
			.build();
	}

	public static <T> ApiResponse<T> created(T data) {
		return ApiResponse.<T>builder()
			.success(true)
			.code("CREATED")
			.message("Created")
			.status(201)
			.data(data)
			.timestamp(Instant.now())
			.build();
	}

	public static ApiResponse<Void> deleted() {
		return ApiResponse.<Void>builder()
			.success(true)
			.code("DELETED")
			.message("Deleted")
			.status(200)
			.timestamp(Instant.now())
			.build();
	}

	public static ApiResponse<Void> error(int status, String code, String message, String path,
		Map<String, String> errors) {
		return ApiResponse.<Void>builder()
			.success(false)
			.code(code)
			.message(message)
			.status(status)
			.timestamp(Instant.now())
			.path(path)
			.errors(errors)
			.build();
	}

	public static ApiResponse<Void> error(int status, ErrorCode errorCode, String message, String path,
		Map<String, String> errors) {
		String resolvedMessage = message != null ? message : errorCode.getDefaultMessage();
		return error(status, errorCode.getCode(), resolvedMessage, path, errors);
	}
}
