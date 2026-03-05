package com.example.hellapi.trade.controller;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.common.api.PageResponse;
import com.example.hellapi.trade.dto.TradeRequest;
import com.example.hellapi.trade.dto.TradeResponse;
import com.example.hellapi.trade.service.TradeService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/trades")
public class TradeController {

	private final TradeService tradeService;

	public TradeController(TradeService tradeService) {
		this.tradeService = tradeService;
	}

	@PostMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('TRADE_CREATE')")
	public ResponseEntity<ApiResponse<TradeResponse>> create(@Valid @RequestBody TradeRequest request) {
		TradeResponse response = TradeResponse.from(tradeService.create(request));
		return ResponseEntity.created(
			ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.getId())
				.toUri()
		).body(ApiResponse.created(response));
	}

	@GetMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('TRADE_READ') and (!#includeDeleted or hasAuthority('TRADE_LIST_DELETED')))")
	public ApiResponse<PageResponse<TradeResponse>> getAll(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		Page<TradeResponse> page = tradeService.getAll(pageable, includeDeleted).map(TradeResponse::from);
		return ApiResponse.ok(PageResponse.from(page));
	}

	@GetMapping("/deleted")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('TRADE_LIST_DELETED')")
	public ApiResponse<PageResponse<TradeResponse>> getDeleted(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.ok(PageResponse.from(tradeService.getDeleted(pageable).map(TradeResponse::from)));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('TRADE_READ') and (!#includeDeleted or hasAuthority('TRADE_LIST_DELETED')))")
	public ApiResponse<TradeResponse> getById(
		@PathVariable Long id,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		return ApiResponse.ok(TradeResponse.from(tradeService.getById(id, includeDeleted)));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('TRADE_UPDATE')")
	public ApiResponse<TradeResponse> update(@PathVariable Long id, @Valid @RequestBody TradeRequest request) {
		return ApiResponse.ok(TradeResponse.from(tradeService.update(id, request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('TRADE_DELETE')")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		tradeService.delete(id);
		return ApiResponse.deleted();
	}

	@PutMapping("/{id}/restore")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('TRADE_RESTORE')")
	public ApiResponse<TradeResponse> restore(@PathVariable Long id) {
		return ApiResponse.ok(TradeResponse.from(tradeService.restore(id)));
	}
}
