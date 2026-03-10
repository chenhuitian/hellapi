package com.example.hellapi.vessel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "vessels")
@SQLDelete(sql = "UPDATE vessels SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vessel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(length = 16)
	private String imo;

	@Column(length = 16)
	private String mmsi;

	@Column(name = "call_sign", length = 16)
	private String callSign;

	@Column(length = 2)
	private String flag;

	@Column(length = 64)
	private String type;

	@Column(nullable = false)
	private boolean deleted;

	@Column(name = "deleted_at")
	private Instant deletedAt;
}

