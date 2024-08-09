package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.adapter.InstantAdapter;
import com.fiap58.pagamento.dto.PagamentoDto;
import com.fiap58.pagamento.interfaces.IQueuePublisher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class QueuePublisher implements IQueuePublisher {

    @Autowired
    private SqsTemplate sqsTemplate;

    @Value("${events.queue-pagamentos-criados}")
    private String queuePagamentosCriados;

    @Value("${events.queue-pagamentos-confirmados}")
    private String queuePagamentosConfirmados;

    @Value("${events.queue-pagamentos-cancelados}")
    private String queuePagamentosCancelados;

    @Override
    public void publicarPagamento(PagamentoDto pagamentoDto, String queueName) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Instant.class, new InstantAdapter()).create();
        String pagamentoMsg = gson.toJson(pagamentoDto);
        System.out.printf("Enviando pagamento à queue %s: %s", queueName, pagamentoMsg);
        sqsTemplate.send(to -> to.queue(queueName)
                .payload(pagamentoMsg)
        );
    }

    @Override
    public void publicarPagamentoCriado(PagamentoDto pagamentoDto) {
        this.publicarPagamento(pagamentoDto, this.queuePagamentosCriados);
    }

    @Override
    public void publicarPagamentoConfirmado(PagamentoDto pagamentoDto) {
        this.publicarPagamento(pagamentoDto, this.queuePagamentosConfirmados);
    }

    @Override
    public void publicarPagamentoCancelado(PagamentoDto pagamentoDto) {
        this.publicarPagamento(pagamentoDto, this.queuePagamentosCancelados);
    }
}
