package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.dto.PagamentoDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DBGatewayTest {

    @Autowired
    private DBGateway dbGateway;

    @Test
    void savePagamento() {
        PagamentoDto pagamentoDto1 = new PagamentoDto(1L, new BigDecimal("10"));

        PagamentoDto pagamentoRes = this.dbGateway.savePagamento(pagamentoDto1);
        assertEquals(pagamentoDto1, pagamentoRes);

    }

    @Test
    void listarPagamentos() {

        List<PagamentoDto> pagamentos = this.dbGateway.listarPagamentos(1);
        assertThat(pagamentos.size(), equalTo(1));
    }

    @Test
    void listarPagamentos_Unbounded() {

        List<PagamentoDto> pagamentos = this.dbGateway.listarPagamentos(-1);
        assertThat(pagamentos.size(), greaterThanOrEqualTo(2));
    }

    @Test
    void buscarPagamento() {

        Optional<PagamentoDto> pagamento = this.dbGateway.buscarPagamento(1L);
        assertTrue(pagamento.isPresent());
    }

    @Test
    void buscarPagamentoPorQrCode() {
        Optional<PagamentoDto> pagamento = this.dbGateway.buscarPagamentoPorQrCode("XXXXX");
        if (pagamento.isPresent()) {
            assertEquals(pagamento.get().getIdPedido(), 4L);
        }
    }

    @Test
    void buscarPagamentoPorIdPedido() {
        Optional<PagamentoDto> pagamento = this.dbGateway.buscarPagamentoPorIdPedido(4L);
        assertTrue(pagamento.isPresent());
    }

    @Test
    void buscarPagamentoPorIdPedido_NaoExiste() {
        Optional<PagamentoDto> pagamento = this.dbGateway.buscarPagamentoPorIdPedido(72L);
        assertFalse(pagamento.isPresent());
    }

}