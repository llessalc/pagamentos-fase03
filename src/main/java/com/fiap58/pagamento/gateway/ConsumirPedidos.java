package com.fiap58.pagamento.gateway;


import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.interfaces.ConsumerPedidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ConsumirPedidos implements ConsumerPedidos {

    @Autowired
    private Environment environment;

    @Override
    public DadosPedidoDto retornarPedido(Long id) {
        String pedidosService = environment.getProperty("pedidos.service");
        RestTemplate restTemplate = new RestTemplate();

        String url_padrao = String.format("http://%s:8080/pedidos", pedidosService);
        StringBuilder urlBuilder = new StringBuilder();
        String url = urlBuilder.append(url_padrao).append("/").append(id).toString();


        ResponseEntity<DadosPedidoDto> responseEntity = restTemplate.getForEntity(url, DadosPedidoDto.class);
        return responseEntity.getBody();
    }

    @Override
    public DadosPedidoDto confirmaPagamento(Long id) {
        RestTemplate restTemplate = new RestTemplate();
        String pedidosService = environment.getProperty("pedidos.service");
        String url_padrao = String.format("http://%s:8080/pedidos", pedidosService);
        StringBuilder urlBuilder = new StringBuilder();
        String url = urlBuilder.append(url_padrao).append("/confirmacao-pagamento/").append(id).toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<DadosPedidoDto> responseEntity = restTemplate.postForEntity(url, "", DadosPedidoDto.class);
        return responseEntity.getBody();
    }
}
