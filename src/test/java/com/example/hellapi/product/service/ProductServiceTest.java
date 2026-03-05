package com.example.hellapi.product.service;

import com.example.hellapi.product.dto.ProductRequest;
import com.example.hellapi.product.entity.Product;
import com.example.hellapi.product.exception.ProductNotDeletedException;
import com.example.hellapi.product.exception.ProductNotFoundException;
import com.example.hellapi.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductService productService;

	@Test
	void create_savesProduct() {
		ProductRequest request = ProductRequest.builder()
			.name("Laptop")
			.price(new BigDecimal("999.99"))
			.description("A laptop")
			.build();

		Product saved = Product.builder()
			.id(1L)
			.name(request.getName())
			.price(request.getPrice())
			.description(request.getDescription())
			.deleted(false)
			.build();

		when(productRepository.save(any(Product.class))).thenReturn(saved);

		Product result = productService.create(request);

		ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
		verify(productRepository).save(captor.capture());
		Product captured = captor.getValue();
		assertThat(captured.getName()).isEqualTo("Laptop");
		assertThat(captured.getPrice()).isEqualByComparingTo("999.99");
		assertThat(captured.getDescription()).isEqualTo("A laptop");
		assertThat(captured.isDeleted()).isFalse();
		assertThat(result.getId()).isEqualTo(1L);
	}

	@Test
	void getById_notFound_throwsProductNotFoundException() {
		when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.getById(1L, false))
			.isInstanceOf(ProductNotFoundException.class)
			.hasMessageContaining("1");
	}

	@Test
	void getById_includeDeleted_findsDeletedProduct() {
		Product product = Product.builder().id(1L).name("X").price(BigDecimal.ONE).deleted(true).build();
		when(productRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(product));

		Product result = productService.getById(1L, true);

		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.isDeleted()).isTrue();
	}

	@Test
	void update_notFound_throwsProductNotFoundException() {
		ProductRequest request = ProductRequest.builder().name("X").price(BigDecimal.ONE).build();
		when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.update(1L, request))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void update_updatesAndSavesProduct() {
		Product existing = Product.builder()
			.id(1L)
			.name("Old")
			.price(BigDecimal.ONE)
			.description("Old desc")
			.deleted(false)
			.build();
		ProductRequest request = ProductRequest.builder()
			.name("New")
			.price(new BigDecimal("99.99"))
			.description("New desc")
			.build();

		when(productRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(existing));
		when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

		Product result = productService.update(1L, request);

		assertThat(result.getName()).isEqualTo("New");
		assertThat(result.getPrice()).isEqualByComparingTo("99.99");
		assertThat(result.getDescription()).isEqualTo("New desc");
	}

	@Test
	void delete_notFound_throwsProductNotFoundException() {
		when(productRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);

		assertThatThrownBy(() -> productService.delete(1L))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void delete_callsRepositoryDelete() {
		when(productRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);

		productService.delete(1L);

		verify(productRepository).deleteById(1L);
	}

	@Test
	void restore_notFound_throwsProductNotFoundException() {
		when(productRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> productService.restore(1L))
			.isInstanceOf(ProductNotFoundException.class);
	}

	@Test
	void restore_notDeleted_throwsProductNotDeletedException() {
		Product product = Product.builder().id(1L).name("X").price(BigDecimal.ONE).deleted(false).build();
		when(productRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(product));

		assertThatThrownBy(() -> productService.restore(1L))
			.isInstanceOf(ProductNotDeletedException.class);
	}

	@Test
	void restore_deletedProduct_restoresSuccessfully() {
		Product deleted = Product.builder()
			.id(1L)
			.name("X")
			.price(BigDecimal.ONE)
			.deleted(true)
			.build();
		when(productRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(deleted));
		when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

		Product result = productService.restore(1L);

		assertThat(result.isDeleted()).isFalse();
		assertThat(result.getDeletedAt()).isNull();
	}

	@Test
	void getAll_returnsPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> page = new PageImpl<>(java.util.List.of(), pageable, 0);
		when(productRepository.findAllByDeletedFalse(pageable)).thenReturn(page);

		Page<Product> result = productService.getAll(pageable, false);

		assertThat(result).isNotNull();
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	void getDeleted_returnsDeletedPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> page = new PageImpl<>(java.util.List.of(), pageable, 0);
		when(productRepository.findAllDeleted(pageable)).thenReturn(page);

		Page<Product> result = productService.getDeleted(pageable);

		assertThat(result).isNotNull();
	}
}
