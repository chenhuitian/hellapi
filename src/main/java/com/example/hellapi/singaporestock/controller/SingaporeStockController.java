package com.example.hellapi.singaporestock.controller;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.common.api.PageResponse;
import com.example.hellapi.singaporestock.dto.MoormooStatusResponse;
import com.example.hellapi.singaporestock.dto.PlaceOrderRequest;
import com.example.hellapi.singaporestock.dto.SingaporeStockOrderResponse;
import com.example.hellapi.singaporestock.service.SingaporeStockService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/singapore-stocks")
public class SingaporeStockController {

	private final SingaporeStockService singaporeStockService;

	public SingaporeStockController(SingaporeStockService singaporeStockService) {
		this.singaporeStockService = singaporeStockService;
	}

	@PostMapping("/orders")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('SINGAPORE_STOCK_TRADE')")
	public ResponseEntity<ApiResponse<SingaporeStockOrderResponse>> placeOrder(
		@Valid @RequestBody PlaceOrderRequest request) {
		var order = singaporeStockService.placeOrder(request);
		return ResponseEntity.created(
			ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(order.getId())
				.toUri()
		).body(ApiResponse.created(SingaporeStockOrderResponse.from(order)));
	}

	@GetMapping("/orders")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('SINGAPORE_STOCK_READ')")
	public ApiResponse<PageResponse<SingaporeStockOrderResponse>> listOrders(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		Page<SingaporeStockOrderResponse> page = singaporeStockService.getAll(pageable, includeDeleted)
			.map(SingaporeStockOrderResponse::from);
		return ApiResponse.ok(PageResponse.from(page));
	}

	@GetMapping("/orders/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('SINGAPORE_STOCK_READ')")
	public ApiResponse<SingaporeStockOrderResponse> getOrder(
		@PathVariable Long id,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		return ApiResponse.ok(SingaporeStockOrderResponse.from(singaporeStockService.getById(id, includeDeleted)));
	}

	@DeleteMapping("/orders/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('SINGAPORE_STOCK_DELETE')")
	public ApiResponse<Void> deleteOrder(@PathVariable Long id) {
		singaporeStockService.delete(id);
		return ApiResponse.deleted();
	}

	@GetMapping("/status")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('SINGAPORE_STOCK_READ')")
	public ApiResponse<MoormooStatusResponse> moormooStatus() {
		return ApiResponse.ok(new MoormooStatusResponse(singaporeStockService.isMoormooAvailable()));
	}
}
