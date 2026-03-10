package com.example.hellapi.i18n;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifies French error messages from messages_fr.properties are returned
 * when Accept-Language: fr is sent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FrenchErrorMessagesIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void productNotFound_withFrenchLocale_returnsFrenchMessage() throws Exception {
		long nonExistentId = 7L; // small id avoids French thousands separator (e.g. 999 999)

		mockMvc.perform(get("/api/products/{id}", nonExistentId)
				.with(httpBasic("admin", "admin123"))
				.header("Accept-Language", "fr"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("Produit introuvable\u00a0: 7"));
	}

	@Test
	void validationError_withFrenchLocale_returnsFrenchMessage() throws Exception {
		// Empty/invalid product body triggers validation
		mockMvc.perform(post("/api/products")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}")
				.header("Accept-Language", "fr"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("\u00c9chec de la validation"));
	}

}
