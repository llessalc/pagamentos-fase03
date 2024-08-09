package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.adapter.PedidoMsgToPedidoDto;
import com.fiap58.pagamento.core.usecases.PagamentoUseCases;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.interfaces.IQueueConsumer;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer implements IQueueConsumer {

    @Autowired
    private PagamentoUseCases pagamentoUseCases;


    //The default ack is ON_SUCCESS.
    @Override
    @SqsListener(value="${events.queue-pedidos}", pollTimeoutSeconds="20")
    public DadosPedidoDto consumirPedidos(String message) {
        System.out.printf("Mensagem recebida da queue: %s%n", message);

        DadosPedidoDto dadosPedidoDto = PedidoMsgToPedidoDto.pedidoMsgToPedidoDto(message);

        pagamentoUseCases.criarPagamento(dadosPedidoDto);

        System.out.printf("Pedido convertido: %s.", dadosPedidoDto);
        return dadosPedidoDto;

    }

}
