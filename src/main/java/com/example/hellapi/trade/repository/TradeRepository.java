package com.example.hellapi.trade.repository;

import com.example.hellapi.trade.entity.Trade;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TradeRepository extends JpaRepository<Trade, Long> {

	Page<Trade> findAllByDeletedFalse(Pageable pageable);

	Optional<Trade> findByIdAndDeletedFalse(Long id);

	boolean existsByIdAndDeletedFalse(Long id);

	@Query(value = "SELECT * FROM trades WHERE deleted = true",
		countQuery = "SELECT COUNT(*) FROM trades WHERE deleted = true",
		nativeQuery = true)
	Page<Trade> findAllDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM trades",
		countQuery = "SELECT COUNT(*) FROM trades",
		nativeQuery = true)
	Page<Trade> findAllIncludeDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM trades WHERE id = :id", nativeQuery = true)
	Optional<Trade> findByIdIncludeDeleted(@Param("id") Long id);
}
