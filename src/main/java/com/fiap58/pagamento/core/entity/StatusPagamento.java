package com.fiap58.pagamento.core.entity;

public enum StatusPagamento {

    CRIADO(1,"Criado"),
    PAGO(2, "Pago"),
    CANCELADO(3, "Cancelado");

    private final int valor;
    private final String status;

    StatusPagamento(int valor, String status){
        this.valor = valor;
        this.status = status;
    }

    public int getValor() {
        return valor;
    }

    public String getStatus() {
        return status;
    }
}
