package com.fiap58.pagamento.dto;

import java.math.BigDecimal;

public record ProdutoCarrinhoSaida(
            String nome,
            int quantidade,
            String precoAtual) {
}
