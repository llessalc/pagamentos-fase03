package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.dto.DadosPedidoDto;

public interface IConsumerPedidos {
    DadosPedidoDto retornarPedido(Long id);
    DadosPedidoDto confirmaPagamento(Long id);
}
