package com.example.hellapi.security;

import com.example.hellapi.security.dto.RoleRequest;
import com.example.hellapi.security.dto.RoleResponse;
import com.example.hellapi.security.dto.RoleUpdatePermissionsRequest;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;

	@Transactional
	public RoleResponse create(RoleRequest request) {
		if (roleRepository.existsByName(request.getName())) {
			throw new RoleNameExistsException(request.getName());
		}
		Role role = Role.builder()
			.name(request.getName())
			.permissions(request.getPermissions() != null ? new HashSet<>(request.getPermissions()) : new HashSet<>())
			.build();
		role = roleRepository.save(role);
		return RoleResponse.from(role);
	}

	@Transactional(readOnly = true)
	public List<RoleResponse> findAll() {
		return roleRepository.findAll().stream().map(RoleResponse::from).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public RoleResponse findById(Long id) {
		Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
		return RoleResponse.from(role);
	}

	@Transactional(readOnly = true)
	public RoleResponse findByName(String name) {
		Role role = roleRepository.findByName(name).orElseThrow(() -> new RoleNotFoundException(name));
		return RoleResponse.from(role);
	}

	@Transactional
	public RoleResponse updatePermissions(Long id, RoleUpdatePermissionsRequest request) {
		Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
		role.setPermissions(request.getPermissions() != null ? new HashSet<>(request.getPermissions()) : new HashSet<>());
		role = roleRepository.save(role);
		return RoleResponse.from(role);
	}
}
