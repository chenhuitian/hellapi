package com.example.hellapi.security;

import com.example.hellapi.security.dto.RoleRequest;
import com.example.hellapi.security.dto.RoleUpdatePermissionsRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoleControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void createRole() throws Exception {
		RoleRequest request = RoleRequest.builder()
			.name("EDITOR")
			.permissions(Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE, Permission.PRODUCT_UPDATE))
			.build();

		MvcResult result = mockMvc.perform(post("/api/roles")
				.with(httpBasic("systemadmin", "systemadmin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").exists())
			.andExpect(jsonPath("$.data.name").value("EDITOR"))
			.andExpect(jsonPath("$.data.permissions").isArray())
			.andReturn();

		JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
		long id = json.get("data").get("id").asLong();
		assertThat(id).isPositive();
	}

	@Test
	void listRoles() throws Exception {
		mockMvc.perform(get("/api/roles").with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(2)));
	}

	@Test
	void getRoleById() throws Exception {
		// Seed data has USER (id 1) and ADMIN (id 2)
		mockMvc.perform(get("/api/roles/1").with(httpBasic("systemadmin", "systemadmin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.name").value("USER"))
			.andExpect(jsonPath("$.data.permissions").isArray());
	}

	@Test
	void updatePermissions() throws Exception {
		RoleUpdatePermissionsRequest request = RoleUpdatePermissionsRequest.builder()
			.permissions(Set.of(Permission.PRODUCT_READ, Permission.PRODUCT_CREATE, Permission.ROLE_MANAGE))
			.build();

		mockMvc.perform(put("/api/roles/1/permissions")
				.with(httpBasic("systemadmin", "systemadmin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.permissions").isArray())
			.andExpect(jsonPath("$.data.permissions.length()").value(3));
	}

	@Test
	void getRoleById_notFound_returns404() throws Exception {
		mockMvc.perform(get("/api/roles/99999").with(httpBasic("systemadmin", "systemadmin123")))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ROLE_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("Role not found"));
	}

	@Test
	void createRole_duplicateName_returns409() throws Exception {
		RoleRequest request = RoleRequest.builder()
			.name("USER")
			.permissions(Set.of(Permission.PRODUCT_READ))
			.build();

		mockMvc.perform(post("/api/roles")
				.with(httpBasic("systemadmin", "systemadmin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("ROLE_NAME_EXISTS"));
	}

	@Test
	void createRole_validationFails_returns400() throws Exception {
		RoleRequest request = RoleRequest.builder()
			.name("")
			.build();

		mockMvc.perform(post("/api/roles")
				.with(httpBasic("systemadmin", "systemadmin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}
}
