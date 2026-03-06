package com.example.hellapi.singaporestock.repository;

import com.example.hellapi.singaporestock.entity.SingaporeStockOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SingaporeStockOrderRepository extends JpaRepository<SingaporeStockOrder, Long> {

	Page<SingaporeStockOrder> findAllByDeletedFalse(Pageable pageable);

	Optional<SingaporeStockOrder> findByIdAndDeletedFalse(Long id);

	boolean existsByIdAndDeletedFalse(Long id);

	@Query(value = "SELECT * FROM singapore_stock_orders WHERE deleted = true",
		countQuery = "SELECT COUNT(*) FROM singapore_stock_orders WHERE deleted = true",
		nativeQuery = true)
	Page<SingaporeStockOrder> findAllDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM singapore_stock_orders",
		countQuery = "SELECT COUNT(*) FROM singapore_stock_orders",
		nativeQuery = true)
	Page<SingaporeStockOrder> findAllIncludeDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM singapore_stock_orders WHERE id = :id", nativeQuery = true)
	Optional<SingaporeStockOrder> findByIdIncludeDeleted(@Param("id") Long id);

	/** BUY orders with status SUBMITTED or FILLED that have not been auto-sold. */
	@Query(value = "SELECT * FROM singapore_stock_orders WHERE side = 'BUY' AND status IN ('SUBMITTED', 'FILLED') AND auto_sold = false AND deleted = false",
		nativeQuery = true)
	List<SingaporeStockOrder> findBuyOrdersNotAutoSold();
}
