package com.fiap58.pagamento.dto;

public record QrCodeDto(
        String in_store_order_id,
        String qr_data
) {

}
