package com.fiap58.pagamento.dto;

import com.fiap58.pagamento.core.entity.Pagamento;

import java.math.BigDecimal;
import java.util.List;

public record QrCodeReq (
        String description,
        String external_reference,
        List<ItemDto> items,
        String notification_url,
        String title,
        BigDecimal total_amount,
        String expiration_date
){
    public QrCodeReq(Pagamento pagamento, List<ItemDto> items, String notificationUrl, String expirationDate){
        this("Pedido", String.valueOf(pagamento.getIdPedido()), items, notificationUrl,
                "Product Order", pagamento.getValorTotal(), expirationDate);
    }


}
