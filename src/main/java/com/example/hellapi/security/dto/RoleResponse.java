package com.example.hellapi.security.dto;

import com.example.hellapi.security.Permission;
import com.example.hellapi.security.Role;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

	private Long id;
	private String name;
	private Set<Permission> permissions;

	public static RoleResponse from(Role role) {
		return RoleResponse.builder()
			.id(role.getId())
			.name(role.getName())
			.permissions(role.getPermissions() != null ? role.getPermissions() : Set.of())
			.build();
	}
}
