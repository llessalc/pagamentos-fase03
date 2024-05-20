package com.fiap58.pagamento.dto;

import java.time.Instant;
import java.util.List;

public record DadosPedidoDto(
        List<ProdutoCarrinhoSaidaDto> produtos,
        String nomeCliente,
        Instant dataPedido,
        String status) {
}
