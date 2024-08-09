package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.usecases.PagamentoUseCases;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaidaDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueueConsumerTest {

    AutoCloseable openMocks;

    @Mock
    private PagamentoUseCases pagamentoUseCases;

    @InjectMocks
    private QueueConsumer queueConsumer;

    @BeforeEach
    public void initMocks(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        openMocks.close();
    }

    @Test
    void consumirPedidos() {
        //Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Long currentEpoch = currentTime.getEpochSecond();
        produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", 1, "15.00"));
        DadosPedidoDto pedidoDtoSample = new DadosPedidoDto(10L, produtos, currentTime,"CRIADO");

        String pedidoMsg = new StringBuilder()
                .append("{ \"id\": 10, \"produtos\": [{\"nome\": \"hamburg\", \"quantidade\": 1,")
                .append("\"precoAtual\": \"15.00\"}],")
                .append("\"dataPedido\": ")
                .append(currentEpoch.toString())
                .append(", \"status\": \"CRIADO\"}")
                .toString();

        DadosPedidoDto parsedMsg = queueConsumer.consumirPedidos(pedidoMsg);

        assertEquals(pedidoDtoSample, parsedMsg);
        verify(pagamentoUseCases, times(1)).criarPagamento(any(DadosPedidoDto.class));

    }
}