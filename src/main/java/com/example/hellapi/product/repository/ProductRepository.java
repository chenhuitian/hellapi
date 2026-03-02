package com.example.hellapi.product.repository;

import com.example.hellapi.product.entity.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	Page<Product> findAllByDeletedFalse(Pageable pageable);

	Optional<Product> findByIdAndDeletedFalse(Long id);

	boolean existsByIdAndDeletedFalse(Long id);

	@Query(value = "SELECT * FROM products WHERE deleted = true",
		countQuery = "SELECT COUNT(*) FROM products WHERE deleted = true",
		nativeQuery = true)
	Page<Product> findAllDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM products",
		countQuery = "SELECT COUNT(*) FROM products",
		nativeQuery = true)
	Page<Product> findAllIncludeDeleted(Pageable pageable);

	@Query(value = "SELECT * FROM products WHERE id = :id", nativeQuery = true)
	Optional<Product> findByIdIncludeDeleted(@Param("id") Long id);
}
