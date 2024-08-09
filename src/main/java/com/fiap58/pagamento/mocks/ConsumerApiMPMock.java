package com.fiap58.pagamento.mocks;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.PagamentoWhStatusDto;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.interfaces.IConsumerApiMP;
import org.springframework.stereotype.Service;

@Service
public class ConsumerApiMPMock implements IConsumerApiMP {


    @Override
    public QrCodeDto retornaQrCode(Pagamento pagamento) {

        return new QrCodeDto("id300002xxx", "123490xxxx500");

    }

    @Override
    public PagamentoWhStatusDto retornaPagamentoStatus(String url) {

        return new PagamentoWhStatusDto("closed", 1);

    }


}
