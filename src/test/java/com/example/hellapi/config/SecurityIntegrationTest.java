package com.example.hellapi.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void apiProducts_withoutAuth_returns401() throws Exception {
		mockMvc.perform(get("/api/products"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void apiProducts_withInvalidCredentials_returns401() throws Exception {
		mockMvc.perform(get("/api/products").with(httpBasic("admin", "wrongpassword")))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void apiProducts_withValidAuth_returns200() throws Exception {
		mockMvc.perform(get("/api/products").with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk());
	}

	@Test
	void apiRoles_userWithoutRoleManage_returns403() throws Exception {
		// USER has PRODUCT_READ, PRODUCT_CREATE, PRODUCT_UPDATE but NOT ROLE_MANAGE
		mockMvc.perform(get("/api/roles").with(httpBasic("user", "user123")))
			.andExpect(status().isForbidden());
	}

	@Test
	void apiRoles_adminWithRoleManage_returns200() throws Exception {
		mockMvc.perform(get("/api/roles").with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk());
	}

	@Test
	void apiProducts_delete_userWithoutProductDelete_returns403() throws Exception {
		// USER has PRODUCT_CREATE, PRODUCT_UPDATE but not PRODUCT_DELETE
		// Seed data has products 1,2,3 - try to delete product 1 as user
		mockMvc.perform(delete("/api/products/1")
				.with(httpBasic("user", "user123")))
			.andExpect(status().isForbidden());
	}

	@Test
	void swaggerUi_withoutAuth_returns200() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
			.andExpect(status().isOk());
	}

}
