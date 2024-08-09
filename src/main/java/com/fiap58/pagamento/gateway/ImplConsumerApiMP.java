package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.ItemDto;
import com.fiap58.pagamento.dto.PagamentoWhStatusDto;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.dto.QrCodeReq;
import com.fiap58.pagamento.interfaces.IConsumerApiMP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImplConsumerApiMP implements IConsumerApiMP {

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public QrCodeDto retornaQrCode(Pagamento pagamento) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = environment.getProperty("mp.token");
        String notificationUrl = environment.getProperty("pagamento.wh");
        headers.set("Authorization", "Bearer " + token);

        String apiMercadoPago = environment.getProperty("qr.url");
        System.out.println("API MERCADO PAGO:");
        System.out.println(environment.getProperty("qr.url"));

        //Set expirationDate - now + 3 days - for the new order
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(3);
        String expirationDateFormatted = formatter.withZone(ZoneOffset.ofHours(-3)).format(expirationDate);

        List<ItemDto> itens = new ArrayList<>();
        itens.add(new ItemDto(pagamento));
        QrCodeReq qrCodeReq = new QrCodeReq(pagamento, itens, notificationUrl, expirationDateFormatted);

        HttpEntity<QrCodeReq> requestEntity = new HttpEntity<>(qrCodeReq, headers);

        ResponseEntity<QrCodeDto> qrCodeDtoResponseEntity = restTemplate.postForEntity(apiMercadoPago,
                requestEntity, QrCodeDto.class);

        return qrCodeDtoResponseEntity.getBody();
    }

    @Override
    public PagamentoWhStatusDto retornaPagamentoStatus(String url) {

        System.out.println(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String token = environment.getProperty("mp.token");
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PagamentoWhStatusDto> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                PagamentoWhStatusDto.class);

        return response.getBody();

    }
}