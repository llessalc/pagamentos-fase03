package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.dto.PagamentoDto;

import java.util.List;
import java.util.Optional;

public interface IDB {

    PagamentoDto savePagamento(PagamentoDto pagamentoDto);
    List<PagamentoDto> listarPagamentos(int limit);
    Optional<PagamentoDto> buscarPagamento(long id);
    Optional<PagamentoDto> buscarPagamentoPorQrCode(String qrCode);
    Optional<PagamentoDto> buscarPagamentoPorIdPedido(long id);
}
