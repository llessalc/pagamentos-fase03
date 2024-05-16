package com.fiap58.pagamento.core.usecases;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaidaDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoUseCasesTest {

    PagamentoUseCases pagamentoUseCases = new PagamentoUseCases();

    @Test
    void criarPagamento() {
    }

    @Test
    void calculaValorPedido() {
        // Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", i, "11.97"));
        }
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(produtos, "jose", now(), "CRIADO");

        BigDecimal expectedValorPedido = new BigDecimal("35.91");

        //Act
        BigDecimal valorPedido = pagamentoUseCases.calculaValorPedido(dadosPedidoDto);

        //Assert
        assertEquals(expectedValorPedido, valorPedido);

    }

    @Test
    void listarPagamentos() {
    }

    @Test
    void buscarPagamento() {
    }

    @Test
    void buscarPagamentoPorQrCode() {
    }

    @Test
    void buscarPagamentoPorIdPedido() {
    }

    @Test
    void confirmarPagamentoHook() {
    }

    @Test
    void confirmarPagamentoManual() {
    }

    @Test
    void checkOrderNotification() {
        //Arrange
        String url = "https://api.mercadolibre.com/merchant_orders/1234";

        //Act
        Boolean isOrderNotification = pagamentoUseCases.checkOrderNotification(url);

        //Assert
        assertEquals(Boolean.TRUE, isOrderNotification);
    }

    @Test
    void checkOrderNotificationFalse() {
        //Arrange
        String url = "https://api.mercadolibre.com/churrascoefarofagrossa/1234";

        //Act
        Boolean isOrderNotification = pagamentoUseCases.checkOrderNotification(url);

        //Assert
        assertEquals(Boolean.FALSE, isOrderNotification);
    }
}