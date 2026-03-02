package com.example.hellapi.security.dto;

import com.example.hellapi.security.Permission;
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
public class RoleUpdatePermissionsRequest {

	private Set<Permission> permissions;
}
