package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.PagamentoWhStatusDto;
import com.fiap58.pagamento.dto.QrCodeDto;

public interface IConsumerApiMP {
    QrCodeDto retornaQrCode(Pagamento pagamento);

    PagamentoWhStatusDto retornaPagamentoStatus(String url);
}
