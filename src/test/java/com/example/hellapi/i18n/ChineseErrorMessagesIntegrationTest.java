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
 * Verifies Chinese error messages from messages_zh_CN.properties are returned
 * when Accept-Language: zh-CN is sent.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChineseErrorMessagesIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void productNotFound_withChineseLocale_returnsChineseMessage() throws Exception {
		long nonExistentId = 7L;

		mockMvc.perform(get("/api/products/{id}", nonExistentId)
				.with(httpBasic("admin", "admin123"))
				.header("Accept-Language", "zh-CN"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("PRODUCT_NOT_FOUND"))
			.andExpect(jsonPath("$.message").value("\u5546\u54c1\u4e0d\u5b58\u5728\uff1a7"));
	}

	@Test
	void validationError_withChineseLocale_returnsChineseMessage() throws Exception {
		mockMvc.perform(post("/api/products")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}")
				.header("Accept-Language", "zh-CN"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("\u53c2\u6570\u6821\u9a8c\u5931\u8d25"));
	}
}
