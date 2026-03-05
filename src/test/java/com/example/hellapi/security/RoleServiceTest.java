package com.example.hellapi.security;

import com.example.hellapi.security.dto.RoleRequest;
import com.example.hellapi.security.dto.RoleUpdatePermissionsRequest;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

	@Mock
	private RoleRepository roleRepository;

	@InjectMocks
	private RoleService roleService;

	@Test
	void create_savesRole() {
		RoleRequest request = RoleRequest.builder()
			.name("EDITOR")
			.permissions(Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE))
			.build();

		when(roleRepository.existsByName("EDITOR")).thenReturn(false);

		Role role = Role.builder()
			.id(1L)
			.name("EDITOR")
			.permissions(Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE))
			.build();
		when(roleRepository.save(any(Role.class))).thenReturn(role);

		var result = roleService.create(request);

		ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
		verify(roleRepository).save(captor.capture());
		assertThat(captor.getValue().getName()).isEqualTo("EDITOR");
		assertThat(captor.getValue().getPermissions()).hasSize(2);
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("EDITOR");
	}

	@Test
	void create_duplicateName_throwsRoleNameExistsException() {
		RoleRequest request = RoleRequest.builder().name("ADMIN").permissions(Set.of()).build();
		when(roleRepository.existsByName("ADMIN")).thenReturn(true);

		assertThatThrownBy(() -> roleService.create(request))
			.isInstanceOf(RoleNameExistsException.class);
	}

	@Test
	void findById_notFound_throwsRoleNotFoundException() {
		when(roleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> roleService.findById(1L))
			.isInstanceOf(RoleNotFoundException.class)
			.hasMessageContaining("1");
	}

	@Test
	void findById_returnsRole() {
		Role role = Role.builder().id(1L).name("USER").permissions(Set.of(Permission.PRODUCT_READ)).build();
		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

		var result = roleService.findById(1L);

		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("USER");
		assertThat(result.getPermissions()).contains(Permission.PRODUCT_READ);
	}

	@Test
	void updatePermissions_notFound_throwsRoleNotFoundException() {
		RoleUpdatePermissionsRequest request = RoleUpdatePermissionsRequest.builder()
			.permissions(Set.of(Permission.PRODUCT_READ))
			.build();
		when(roleRepository.findById(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> roleService.updatePermissions(1L, request))
			.isInstanceOf(RoleNotFoundException.class);
	}

	@Test
	void updatePermissions_updatesAndSaves() {
		Role role = Role.builder()
			.id(1L)
			.name("USER")
			.permissions(new java.util.HashSet<>(Set.of(Permission.PRODUCT_READ)))
			.build();
		RoleUpdatePermissionsRequest request = RoleUpdatePermissionsRequest.builder()
			.permissions(Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE))
			.build();

		when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
		when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

		var result = roleService.updatePermissions(1L, request);

		ArgumentCaptor<Role> captor = ArgumentCaptor.forClass(Role.class);
		verify(roleRepository).save(captor.capture());
		assertThat(captor.getValue().getPermissions()).containsExactlyInAnyOrder(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE);
		assertThat(result.getPermissions()).hasSize(2);
	}
}
