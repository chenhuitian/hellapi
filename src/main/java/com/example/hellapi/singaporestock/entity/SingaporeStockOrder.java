package com.example.hellapi.singaporestock.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "singapore_stock_orders")
@SQLDelete(sql = "UPDATE singapore_stock_orders SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingaporeStockOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 32)
	private String symbol;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 8)
	private OrderSide side;

	@Column(nullable = false)
	private Integer quantity;

	@Column(nullable = false, precision = 19, scale = 4)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private OrderType orderType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 32)
	private OrderStatus status;

	@Column(name = "external_order_id", length = 64)
	private String externalOrderId;

	@Column(length = 500)
	private String message;

	/** True when this BUY order was auto-sold by the schedule (price >= bought + 10%). */
	@Column(name = "auto_sold", nullable = false)
	private boolean autoSold;

	/** External order ID of the SELL order created by auto-sell, if any. */
	@Column(name = "auto_sell_order_id", length = 64)
	private String autoSellOrderId;

	@Column(nullable = false)
	private boolean deleted;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public enum OrderSide {
		BUY,
		SELL
	}

	public enum OrderType {
		MARKET,
		LIMIT
	}

	public enum OrderStatus {
		PENDING,
		SUBMITTING,
		SUBMITTED,
		FILLED,
		CANCELLED,
		FAILED,
		DISABLED
	}
}
