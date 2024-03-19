package com.fiap58.pagamento.gateway.impl;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ItemDto;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.dto.QrCodeReq;
import com.fiap58.pagamento.gateway.ConsumerApiMP;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImplConsumerApiMP implements ConsumerApiMP {
    @Override
    public QrCodeDto retornaQrCode(Pagamento pagamento) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = "TEST-6367506296567050-012618-3399e405dfebefc329dfc523a63d2f91-483146344";
        headers.set("Authorization", "Bearer " + token);

        String apiMercadoLivre = "https://api.mercadopago.com/instore/orders/qr/seller/collectors/483146344/pos/58/qrs";


        List<ItemDto> itens = new ArrayList<>();
        itens.add(new ItemDto(pagamento));
        QrCodeReq qrCodeReq = new QrCodeReq(pagamento, itens);

        HttpEntity<QrCodeReq> requestEntity = new HttpEntity<>(qrCodeReq, headers);

        ResponseEntity<QrCodeDto> qrCodeDtoResponseEntity = restTemplate.postForEntity(apiMercadoLivre,
                requestEntity, QrCodeDto.class);

        return qrCodeDtoResponseEntity.getBody();
    }
}
