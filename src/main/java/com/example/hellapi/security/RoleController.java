package com.example.hellapi.security;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.security.dto.RoleRequest;
import com.example.hellapi.security.dto.RoleResponse;
import com.example.hellapi.security.dto.RoleUpdatePermissionsRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@PostMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ROLE_MANAGE')")
	public ResponseEntity<ApiResponse<RoleResponse>> create(@Valid @RequestBody RoleRequest request) {
		RoleResponse response = roleService.create(request);
		return ResponseEntity.created(
			ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.getId())
				.toUri()
		).body(ApiResponse.created(response));
	}

	@GetMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ROLE_MANAGE')")
	public ApiResponse<List<RoleResponse>> list() {
		return ApiResponse.ok(roleService.findAll());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ROLE_MANAGE')")
	public ApiResponse<RoleResponse> getById(@PathVariable Long id) {
		return ApiResponse.ok(roleService.findById(id));
	}

	@PutMapping("/{id}/permissions")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('ROLE_MANAGE')")
	public ApiResponse<RoleResponse> updatePermissions(
		@PathVariable Long id,
		@RequestBody RoleUpdatePermissionsRequest request) {
		return ApiResponse.ok(roleService.updatePermissions(id, request));
	}
}
