package com.example.hellapi.job.repository;

import com.example.hellapi.job.entity.Job;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

	Page<Job> findAllByDeletedFalse(Pageable pageable);

	Optional<Job> findByIdAndDeletedFalse(Long id);

	boolean existsByIdAndDeletedFalse(Long id);

	@Query(value = "SELECT * FROM jobs WHERE deleted = true",
		countQuery = "SELECT COUNT(*) FROM jobs WHERE deleted = true",
		nativeQuery = true)
	Page<Job> findAllDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM jobs",
		countQuery = "SELECT COUNT(*) FROM jobs",
		nativeQuery = true)
	Page<Job> findAllIncludeDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM jobs WHERE id = :id", nativeQuery = true)
	Optional<Job> findByIdIncludeDeleted(@Param("id") Long id);
}
