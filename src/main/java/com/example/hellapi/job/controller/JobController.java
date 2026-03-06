package com.example.hellapi.job.controller;

import com.example.hellapi.common.api.ApiResponse;
import com.example.hellapi.common.api.PageResponse;
import com.example.hellapi.job.dto.JobRequest;
import com.example.hellapi.job.dto.JobResponse;
import com.example.hellapi.job.service.JobService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

	private final JobService jobService;

	public JobController(JobService jobService) {
		this.jobService = jobService;
	}

	@PostMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('JOB_CREATE')")
	public ResponseEntity<ApiResponse<JobResponse>> create(@Valid @RequestBody JobRequest request) {
		JobResponse response = JobResponse.from(jobService.create(request));
		return ResponseEntity.created(
			ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.getId())
				.toUri()
		).body(ApiResponse.created(response));
	}

	@GetMapping
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('JOB_READ') and (!#includeDeleted or hasAuthority('JOB_LIST_DELETED')))")
	public ApiResponse<PageResponse<JobResponse>> getAll(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		Page<JobResponse> page = jobService.getAll(pageable, includeDeleted).map(JobResponse::from);
		return ApiResponse.ok(PageResponse.from(page));
	}

	@GetMapping("/deleted")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('JOB_LIST_DELETED')")
	public ApiResponse<PageResponse<JobResponse>> getDeleted(
		@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.ok(PageResponse.from(jobService.getDeleted(pageable).map(JobResponse::from)));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or (hasAuthority('JOB_READ') and (!#includeDeleted or hasAuthority('JOB_LIST_DELETED')))")
	public ApiResponse<JobResponse> getById(
		@PathVariable Long id,
		@RequestParam(defaultValue = "false") boolean includeDeleted) {
		return ApiResponse.ok(JobResponse.from(jobService.getById(id, includeDeleted)));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('JOB_UPDATE')")
	public ApiResponse<JobResponse> update(@PathVariable Long id, @Valid @RequestBody JobRequest request) {
		return ApiResponse.ok(JobResponse.from(jobService.update(id, request)));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('JOB_DELETE')")
	public ApiResponse<Void> delete(@PathVariable Long id) {
		jobService.delete(id);
		return ApiResponse.deleted();
	}

	@PutMapping("/{id}/restore")
	@PreAuthorize("hasRole('SYSTEM_ADMIN') or hasAuthority('JOB_RESTORE')")
	public ApiResponse<JobResponse> restore(@PathVariable Long id) {
		return ApiResponse.ok(JobResponse.from(jobService.restore(id)));
	}
}
