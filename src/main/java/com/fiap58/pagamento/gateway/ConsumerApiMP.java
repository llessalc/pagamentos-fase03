package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.QrCodeDto;

public interface ConsumerApiMP {
    QrCodeDto retornaQrCode(Pagamento pagamento);
}
