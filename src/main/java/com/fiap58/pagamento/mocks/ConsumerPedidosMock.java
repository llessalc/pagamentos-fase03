package com.fiap58.pagamento.mocks;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaidaDto;
import com.fiap58.pagamento.interfaces.ConsumerPedidos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;


@Service
public class ConsumerPedidosMock implements ConsumerPedidos  {

    @Override
    public DadosPedidoDto retornarPedido(Long id) {
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", i, "30.90"));
        }
        return new DadosPedidoDto(produtos, "jose", now(), "CRIADO");

    }

    @Override
    public DadosPedidoDto confirmaPagamento(Long id) {
        List<ProdutoCarrinhoSaidaDto> produtos = List.of(new ProdutoCarrinhoSaidaDto("hamburg", 1, "19.99"));
        return new DadosPedidoDto(produtos, "jose", now(), "PAGO");

    }

}


