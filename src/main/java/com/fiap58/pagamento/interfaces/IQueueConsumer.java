package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.PagamentoDto;

public interface IQueueConsumer {

    DadosPedidoDto consumirPedidos(String message);

}
