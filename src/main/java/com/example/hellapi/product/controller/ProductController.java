package com.example.hellapi.product.controller;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.common.api.PageResponse;
import com.example.hellapi.product.dto.ProductRequest;
import com.example.hellapi.product.dto.ProductResponse;
import com.example.hellapi.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('PRODUCT_CREATE')")
	public ResponseEntity<ApiResponse<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
		ProductResponse response = ProductResponse.from(productService.create(request));
		return ResponseEntity.created(
			ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.getId())
				.toUri()
		).body(ApiResponse.created(response));
	}

	@GetMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('PRODUCT_READ') and (!#includeDeleted or hasAuthority('PRODUCT_LIST_DELETED')))")
	public ApiResponse<PageResponse<ProductResponse>> getAll(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		Page<ProductResponse> page = productService.getAll(pageable, includeDeleted).map(ProductResponse::from);
		return ApiResponse.ok(PageResponse.from(page));
	}

	@GetMapping("/deleted")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('PRODUCT_LIST_DELETED')")
	public ApiResponse<PageResponse<ProductResponse>> getDeleted(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.ok(PageResponse.from(productService.getDeleted(pageable).map(ProductResponse::from)));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('PRODUCT_READ') and (!#includeDeleted or hasAuthority('PRODUCT_LIST_DELETED')))")
	public ApiResponse<ProductResponse> getById(
		@PathVariable Long id,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		return ApiResponse.ok(ProductResponse.from(productService.getById(id, includeDeleted)));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('PRODUCT_UPDATE')")
	public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
		return ApiResponse.ok(ProductResponse.from(productService.update(id, request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('PRODUCT_DELETE')")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		productService.delete(id);
		return ApiResponse.deleted();
	}

	@PutMapping("/{id}/restore")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('PRODUCT_RESTORE')")
	public ApiResponse<ProductResponse> restore(@PathVariable Long id) {
		return ApiResponse.ok(ProductResponse.from(productService.restore(id)));
	}
}
