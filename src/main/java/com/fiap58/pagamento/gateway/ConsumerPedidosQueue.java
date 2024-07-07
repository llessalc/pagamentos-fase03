package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.interfaces.IConsumerPedidosQueue;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConsumerPedidosQueue implements IConsumerPedidosQueue  {

    @Autowired
    private Environment environment;
    private final String pedidosQueue = environment.getProperty("events.queue-pedidos");

    @Override
    @SqsListener(value=pedidosQueue, pollTimeoutSeconds="20")
    public void consumirPedidos(String message) {
        System.out.printf("Received message from queue is %s%n", message);

    }
}
