package com.fiap58.pagamento.adapter;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaidaDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PedidoMsgToPedidoDtoTest {

    @Test
    void pedidoMsgToPedidoDto() {

        //Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        Instant currentTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Long currentEpoch = currentTime.getEpochSecond();
        produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", 1, "15.00"));
        DadosPedidoDto pedidoDtoSample = new DadosPedidoDto(10L, produtos, currentTime,"CRIADO");

        String pedidoMsg = new StringBuilder()
                                .append("{ \"id\": 10, \"produtos\": [{\"nome\": \"hamburg\", \"quantidade\": 1,")
                                .append("\"precoAtual\": \"15.00\"}],")
                                .append("\"dataPedido\": ")
                                .append(currentEpoch.toString())
                                .append(", \"status\": \"CRIADO\"}")
                                .toString();

        //Act
        DadosPedidoDto parsedMsg = PedidoMsgToPedidoDto.pedidoMsgToPedidoDto(pedidoMsg);

        //Assert
        assertEquals(parsedMsg, pedidoDtoSample);

    }
}