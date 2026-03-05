package com.example.hellapi.singaporestock;

import com.example.hellapi.singaporestock.dto.PlaceOrderRequest;
import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SingaporeStockControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void placeOrder_buy_success() throws Exception {
		PlaceOrderRequest request = PlaceOrderRequest.builder()
			.symbol("D05")
			.side(SingaporeStockOrder.OrderSide.BUY)
			.quantity(100)
			.price(new java.math.BigDecimal("35.50"))
			.orderType(SingaporeStockOrder.OrderType.LIMIT)
			.build();

		mockMvc.perform(post("/api/singapore-stocks/orders")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").exists())
			.andExpect(jsonPath("$.data.symbol").value("D05"))
			.andExpect(jsonPath("$.data.side").value("BUY"))
			.andExpect(jsonPath("$.data.quantity").value(100))
			.andExpect(jsonPath("$.data.price").value(35.50))
			.andExpect(jsonPath("$.data.orderType").value("LIMIT"))
			.andExpect(jsonPath("$.data.status").exists());
	}

	@Test
	void placeOrder_sell_success() throws Exception {
		PlaceOrderRequest request = PlaceOrderRequest.builder()
			.symbol("O39")
			.side(SingaporeStockOrder.OrderSide.SELL)
			.quantity(50)
			.price(new java.math.BigDecimal("14.20"))
			.orderType(SingaporeStockOrder.OrderType.MARKET)
			.build();

		mockMvc.perform(post("/api/singapore-stocks/orders")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.side").value("SELL"))
			.andExpect(jsonPath("$.data.orderType").value("MARKET"));
	}

	@Test
	void listOrders_success() throws Exception {
		mockMvc.perform(get("/api/singapore-stocks/orders")
				.with(httpBasic("admin", "admin123"))
				.param("page", "0")
				.param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.content").isArray());
	}

	@Test
	void moormooStatus_success() throws Exception {
		mockMvc.perform(get("/api/singapore-stocks/status")
				.with(httpBasic("admin", "admin123")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.available").isBoolean());
	}

	@Test
	void placeOrder_validationFails_returns400() throws Exception {
		PlaceOrderRequest request = PlaceOrderRequest.builder()
			.symbol("")
			.side(SingaporeStockOrder.OrderSide.BUY)
			.quantity(0)
			.price(java.math.BigDecimal.ZERO)
			.orderType(SingaporeStockOrder.OrderType.LIMIT)
			.build();

		mockMvc.perform(post("/api/singapore-stocks/orders")
				.with(httpBasic("admin", "admin123"))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
	}
}
