package com.fiap58.pagamento.core.entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Pagamento {

    private Long idPedido;
    private String qrCode;
    private String inStoreOrderId;
    private BigDecimal valorTotal;
    private StatusPagamento status;

    public Pagamento(Long idPedido) {
        this.idPedido = idPedido;
        this.status = StatusPagamento.CRIADO;
    }

}
