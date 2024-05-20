package com.fiap58.pagamento.adapter;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.PagamentoDto;

public class PagamentoToPagamentoDto {

    public static PagamentoDto pagamentoToPagamentoDto(Pagamento pagamento) {
        PagamentoDto pagamentoDto = new PagamentoDto(pagamento.getIdPedido(), pagamento.getValorTotal());
        pagamentoDto.setQrCode(pagamento.getQrCode());
        pagamentoDto.setInStoreOrderId(pagamento.getInStoreOrderId());

        return pagamentoDto;
    }
}
