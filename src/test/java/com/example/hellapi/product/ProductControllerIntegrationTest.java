package com.example.hellapi.product;

import com.example.hellapi.product.dto.ProductRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void crudLifecycle() throws Exception {
		ProductRequest request = ProductRequest.builder()
			.name("Laptop")
			.price(new java.math.BigDecimal("8999.00"))
			.description("Development laptop")
			.build();

		MvcResult createResult = mockMvc.perform(post("/api/products")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").exists())
			.andExpect(jsonPath("$.data.name").value("Laptop"))
			.andReturn();

		JsonNode createJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
		long id = createJson.get("data").get("id").asLong();
		assertThat(id).isPositive();

		mockMvc.perform(get("/api/products/{id}", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").value(id))
			.andExpect(jsonPath("$.data.price").value(8999.00));

		mockMvc.perform(get("/api/products")
				.with(httpBasic("admin", "admin123"))
				.param("page", "0")
				.param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content").isArray())
			.andExpect(jsonPath("$.data.page").value(0));

		ProductRequest update = ProductRequest.builder()
			.name("Laptop Pro")
			.price(new java.math.BigDecimal("9999.00"))
			.description("Upgraded laptop")
			.build();

		mockMvc.perform(put("/api/products/{id}", id)
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(update)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.name").value("Laptop Pro"));

		mockMvc.perform(delete("/api/products/{id}", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		mockMvc.perform(get("/api/products/{id}", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("Product not found: " + id));

		mockMvc.perform(get("/api/products")
				.param("includeDeleted", "true")
				.with(httpBasic("admin", "admin123"))
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray());

		mockMvc.perform(get("/api/products/{id}", id)
				.with(httpBasic("admin", "admin123"))
				.param("includeDeleted", "true"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(id));

		mockMvc.perform(get("/api/products/deleted")
				.with(httpBasic("admin", "admin123"))
				.param("page", "0")
				.param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content").isArray());

		mockMvc.perform(put("/api/products/{id}/restore", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").value(id));

		mockMvc.perform(delete("/api/products/{id}", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk());

		mockMvc.perform(put("/api/products/{id}/restore", id).with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk());
	}
}
