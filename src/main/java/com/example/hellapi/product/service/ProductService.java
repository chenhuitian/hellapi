package com.example.hellapi.product.service;

import com.example.hellapi.product.dto.ProductRequest;
import com.example.hellapi.product.entity.Product;
import com.example.hellapi.product.exception.ProductNotDeletedException;
import com.example.hellapi.product.exception.ProductNotFoundException;
import com.example.hellapi.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Transactional
	public Product create(ProductRequest request) {
		Product product = Product.builder()
			.name(request.getName())
			.price(request.getPrice())
			.description(request.getDescription())
			.deleted(false)
			.build();
		return productRepository.save(product);
	}

	@Transactional(readOnly = true)
	public Page<Product> getAll(Pageable pageable, boolean includeDeleted) {
		if (includeDeleted) {
			return productRepository.findAllIncludeDeleted(pageable);
		}
		return productRepository.findAllByDeletedFalse(pageable);
	}

	@Transactional(readOnly = true)
	public Page<Product> getDeleted(Pageable pageable) {
		return productRepository.findAllDeleted(pageable);
	}

	@Transactional(readOnly = true)
	public Product getById(Long id, boolean includeDeleted) {
		if (includeDeleted) {
			return productRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new ProductNotFoundException(id));
		}
		return productRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ProductNotFoundException(id));
	}

	@Transactional
	public Product update(Long id, ProductRequest request) {
		Product product = productRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new ProductNotFoundException(id));
		product.setName(request.getName());
		product.setPrice(request.getPrice());
		product.setDescription(request.getDescription());
		return productRepository.save(product);
	}

	@Transactional
	public void delete(Long id) {
		if (!productRepository.existsByIdAndDeletedFalse(id)) {
			throw new ProductNotFoundException(id);
		}
		productRepository.deleteById(id);
	}

	@Transactional
	public Product restore(Long id) {
		Product product = productRepository.findByIdIncludeDeleted(id).orElseThrow(() -> new ProductNotFoundException(id));
		if (!product.isDeleted()) {
			throw new ProductNotDeletedException(id);
		}
		product.setDeleted(false);
		product.setDeletedAt(null);
		return productRepository.save(product);
	}
}
