package com.example.hellapi.trade.service;

import com.example.hellapi.trade.dto.TradeRequest;
import com.example.hellapi.trade.entity.Trade;
import com.example.hellapi.trade.exception.TradeNotDeletedException;
import com.example.hellapi.trade.exception.TradeNotFoundException;
import com.example.hellapi.trade.repository.TradeRepository;
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
class TradeServiceTest {

	@Mock
	private TradeRepository tradeRepository;

	@InjectMocks
	private TradeService tradeService;

	@Test
	void create_savesTrade() {
		TradeRequest request = TradeRequest.builder()
			.name("ETH Sale")
			.price(new BigDecimal("3000.00"))
			.description("Ethereum sell order")
			.build();

		Trade saved = Trade.builder()
			.id(1L)
			.name(request.getName())
			.price(request.getPrice())
			.description(request.getDescription())
			.deleted(false)
			.build();

		when(tradeRepository.save(any(Trade.class))).thenReturn(saved);

		Trade result = tradeService.create(request);

		ArgumentCaptor<Trade> captor = ArgumentCaptor.forClass(Trade.class);
		verify(tradeRepository).save(captor.capture());
		Trade captured = captor.getValue();
		assertThat(captured.getName()).isEqualTo("ETH Sale");
		assertThat(captured.getPrice()).isEqualByComparingTo("3000.00");
		assertThat(captured.getDescription()).isEqualTo("Ethereum sell order");
		assertThat(captured.isDeleted()).isFalse();
		assertThat(result.getId()).isEqualTo(1L);
	}

	@Test
	void getById_notFound_throwsTradeNotFoundException() {
		when(tradeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tradeService.getById(1L, false))
			.isInstanceOf(TradeNotFoundException.class)
			.hasMessageContaining("1");
	}

	@Test
	void update_notFound_throwsTradeNotFoundException() {
		TradeRequest request = TradeRequest.builder().name("X").price(BigDecimal.ONE).build();
		when(tradeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> tradeService.update(1L, request))
			.isInstanceOf(TradeNotFoundException.class);
	}

	@Test
	void delete_notFound_throwsTradeNotFoundException() {
		when(tradeRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);

		assertThatThrownBy(() -> tradeService.delete(1L))
			.isInstanceOf(TradeNotFoundException.class);
	}

	@Test
	void restore_notDeleted_throwsTradeNotDeletedException() {
		Trade trade = Trade.builder().id(1L).name("X").price(BigDecimal.ONE).deleted(false).build();
		when(tradeRepository.findByIdIncludeDeleted(1L)).thenReturn(Optional.of(trade));

		assertThatThrownBy(() -> tradeService.restore(1L))
			.isInstanceOf(TradeNotDeletedException.class);
	}
}
