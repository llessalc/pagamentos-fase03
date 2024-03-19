package com.fiap58.pagamento.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "Pagamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGAMENTO")
    private Long idPagamento;

    @Column(name = "ID_PEDIDO")
    private Long idPedido;

    @Column(name = "QR_CODE")
    private String qrCode;

    @Column(name = "IN_STORE_ORDER_ID")
    private String inStoreOrderId;

    @Column(name = "VALOR_TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @Column(name = "CRIADO_EM")
    private Instant criadoEm;
    @Column(name = "ATUALIZADO_EM")
    private Instant atualizadoEm;
    @Column(name = "DELETADO_EM")
    private Instant deletadoEm;

    public Pagamento(long idPedido, BigDecimal valorTotal){
        this.idPedido = idPedido;
        this.valorTotal = valorTotal;
        this.status = StatusPagamento.CRIADO;
        this.criadoEm = Instant.now();
        this.atualizadoEm = Instant.now();
    }
}
