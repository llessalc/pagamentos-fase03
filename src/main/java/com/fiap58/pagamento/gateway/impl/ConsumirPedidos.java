package com.fiap58.pagamento.gateway.impl;


import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.gateway.ConsumerPedidos;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumirPedidos implements ConsumerPedidos {

    String pedidos_service = System.getenv("pedidos_service");

    @Override
    public DadosPedidoDto retornarPedido(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url_padrao = String.format("http://%s:8080/pedidos", this.pedidos_service);
        StringBuilder urlBuilder = new StringBuilder();
        String url = urlBuilder.append(url_padrao).append("/").append(id).toString();


        ResponseEntity<DadosPedidoDto> responseEntity = restTemplate.getForEntity(url, DadosPedidoDto.class);
        return responseEntity.getBody();
    }

    @Override
    public DadosPedidoDto confirmaPagamento(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String url_padrao = String.format("http://%s:8080/pedidos", this.pedidos_service);
        StringBuilder urlBuilder = new StringBuilder();
        String url = urlBuilder.append(url_padrao).append("/confirmacao-pagamento/").append(id).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<DadosPedidoDto> responseEntity = restTemplate.postForEntity(url, "", DadosPedidoDto.class);
        return responseEntity.getBody();
    }
}
