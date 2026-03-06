package com.example.hellapi.job.dto;

import com.example.hellapi.job.entity.Job;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobResponse {

	private Long id;
	private String title;
	private BigDecimal salary;
	private String description;

	public static JobResponse from(Job job) {
		return JobResponse.builder()
			.id(job.getId())
			.title(job.getTitle())
			.salary(job.getSalary())
			.description(job.getDescription())
			.build();
	}
}
