package com.example.hellapi.security;

import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Loads users and their role-based permissions. SYSTEM_ADMIN has full access;
 * ADMIN and USER (and any custom role) get permissions from the roles table.
 */
@Service
public class DbBackedUserDetailsService implements UserDetailsService {

	private static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
	private static final String ADMIN_USERNAME = "admin";
	private static final String ADMIN_PASSWORD = "admin123";
	private static final String NORMAL_USERNAME = "user";
	private static final String NORMAL_PASSWORD = "user123";
	private static final String SYSTEM_ADMIN_USERNAME = "systemadmin";
	private static final String SYSTEM_ADMIN_PASSWORD = "systemadmin123";

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	private final String encodedAdminPassword;
	private final String encodedUserPassword;
	private final String encodedSystemAdminPassword;

	public DbBackedUserDetailsService(RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.encodedAdminPassword = passwordEncoder.encode(ADMIN_PASSWORD);
		this.encodedUserPassword = passwordEncoder.encode(NORMAL_PASSWORD);
		this.encodedSystemAdminPassword = passwordEncoder.encode(SYSTEM_ADMIN_PASSWORD);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (SYSTEM_ADMIN_USERNAME.equalsIgnoreCase(username)) {
			return User.builder()
				.username(SYSTEM_ADMIN_USERNAME)
				.password(encodedSystemAdminPassword)
				.authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + SYSTEM_ADMIN)))
				.build();
		}
		if (ADMIN_USERNAME.equalsIgnoreCase(username)) {
			return buildUserWithRole(ADMIN_USERNAME, encodedAdminPassword, "ADMIN");
		}
		if (NORMAL_USERNAME.equalsIgnoreCase(username)) {
			return buildUserWithRole(NORMAL_USERNAME, encodedUserPassword, "USER");
		}
		throw new UsernameNotFoundException("User not found: " + username);
	}

	private UserDetails buildUserWithRole(String username, String encodedPassword, String roleName) {
		var role = roleRepository.findByName(roleName).orElse(null);
		var authorities = new java.util.ArrayList<SimpleGrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
		if (role != null && role.getPermissions() != null) {
			role.getPermissions().stream()
				.map(p -> new SimpleGrantedAuthority(p.name()))
				.forEach(authorities::add);
		}
		return User.builder()
			.username(username)
			.password(encodedPassword)
			.authorities(authorities)
			.build();
	}
}
