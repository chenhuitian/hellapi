package com.example.hellapi.job.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobRequest {

	@NotBlank(message = "{job.title.notBlank}")
	@Size(max = 255, message = "{job.title.size}")
	private String title;

	@NotNull(message = "{job.salary.notNull}")
	@DecimalMin(value = "0.0", inclusive = false, message = "{job.salary.min}")
	private BigDecimal salary;

	@Size(max = 1000, message = "{job.description.size}")
	private String description;
}
