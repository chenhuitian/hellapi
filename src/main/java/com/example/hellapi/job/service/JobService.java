package com.example.hellapi.job.service;

import com.example.hellapi.job.dto.JobRequest;
import com.example.hellapi.job.entity.Job;
import com.example.hellapi.job.exception.JobNotDeletedException;
import com.example.hellapi.job.exception.JobNotFoundException;
import com.example.hellapi.job.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

	private final JobRepository jobRepository;

	public JobService(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}

	@Transactional
	public Job create(JobRequest request) {
		Job job = Job.builder()
			.title(request.getTitle())
			.salary(request.getSalary())
			.description(request.getDescription())
			.deleted(false)
			.build();
		return jobRepository.save(job);
	}

	@Transactional(readOnly = true)
	public Page<Job> getAll(Pageable pageable, boolean includeDeleted) {
		if (includeDeleted) {
			return jobRepository.findAllIncludeDeleted(pageable);
		}
		return jobRepository.findAllByDeletedFalse(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Job> getDeleted(Pageable pageable) {
		return jobRepository.findAllDeleted(pageable);
	}

	@Transactional(readOnly = true)
	public Job getById(Long id, boolean includeDeleted) {
		if (includeDeleted) {
			return jobRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new JobNotFoundException(id));
		}
		return jobRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new JobNotFoundException(id));
	}

	@Transactional
	public Job update(Long id, JobRequest request) {
		Job job = jobRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new JobNotFoundException(id));
		job.setTitle(request.getTitle());
		job.setSalary(request.getSalary());
		job.setDescription(request.getDescription());
		return jobRepository.save(job);
	}

	@Transactional
	public void delete(Long id) {
		if (!jobRepository.existsByIdAndDeletedFalse(id)) {
			throw new JobNotFoundException(id);
		}
		jobRepository.deleteById(id);
	}

	@Transactional
	public Job restore(Long id) {
		Job job = jobRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new JobNotFoundException(id));
		if (!job.isDeleted()) {
			throw new JobNotDeletedException(id);
		}
		job.setDeleted(false);
		job.setDeletedAt(null);
		return jobRepository.save(job);
	}
}
