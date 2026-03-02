package com.example.hellapi.security.dto;

import com.example.hellapi.security.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

	@NotBlank
	@Size(max = 64)
	private String name;

	private Set<Permission> permissions;
}
