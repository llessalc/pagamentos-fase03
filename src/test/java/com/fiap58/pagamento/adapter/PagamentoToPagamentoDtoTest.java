package com.fiap58.pagamento.adapter;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.core.entity.StatusPagamento;
import com.fiap58.pagamento.dto.PagamentoDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoToPagamentoDtoTest {


    @Test
    void testConversao() {

        //Arrange
        Pagamento pagamento = new Pagamento(1L);
        BigDecimal valorTotal = new BigDecimal("10");
        pagamento.setValorTotal(valorTotal);
        PagamentoDto pagamentoDto = PagamentoToPagamentoDto.pagamentoToPagamentoDto(pagamento);

        assertEquals(pagamento.getValorTotal(), pagamentoDto.getValorTotal());
        assertEquals(StatusPagamento.CRIADO, pagamentoDto.getStatus());

    }
}