package com.fiap58.pagamento.interfaces;

public interface IConsumerPedidosQueue {

    void consumirPedidos(String message);
}
