package com.fiap58.pagamento.dto;

import com.fiap58.pagamento.core.entity.Pagamento;

import java.math.BigDecimal;

public record ItemDto(
        String title,
        BigDecimal unit_price,
        int quantity,
        String unit_measure,
        BigDecimal total_amount
) {
    public ItemDto(Pagamento pagamento){
        this("Prod Unico", pagamento.getValorTotal(), 1, "unit", pagamento.getValorTotal());
    }
}
