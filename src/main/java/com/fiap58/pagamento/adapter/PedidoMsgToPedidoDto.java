package com.fiap58.pagamento.adapter;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

public class PedidoMsgToPedidoDto {

    public static DadosPedidoDto pedidoMsgToPedidoDto(String pedidoMsg) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
        return gson.fromJson(pedidoMsg, DadosPedidoDto.class);
    }
}
