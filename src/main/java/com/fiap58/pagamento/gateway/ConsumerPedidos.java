package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import org.springframework.http.ResponseEntity;

public interface ConsumerPedidos {
    DadosPedidoDto retornarPedido(Long id);
    DadosPedidoDto confirmaPagamento(Long id);
}
