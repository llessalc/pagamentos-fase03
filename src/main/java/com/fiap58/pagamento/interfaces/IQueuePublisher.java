package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.dto.PagamentoDto;

public interface IQueuePublisher {
    void publicarPagamento(PagamentoDto pagamentoDto);
}
