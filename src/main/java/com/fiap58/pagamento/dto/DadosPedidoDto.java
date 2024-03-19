package com.fiap58.pagamento.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record DadosPedidoDto(
        List<ProdutoCarrinhoSaida> produtos,
        String nomeCliente,
        Instant dataPedido,
        String status) {
}
