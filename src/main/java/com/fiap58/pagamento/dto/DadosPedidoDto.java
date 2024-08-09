package com.fiap58.pagamento.dto;

import java.time.Instant;
import java.util.List;

public record DadosPedidoDto(

        Long id,
        List<ProdutoCarrinhoSaidaDto> produtos,
        Instant dataPedido,
        String status) {
}
