package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaidaDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConsumirPedidosTest {

    AutoCloseable openMocks;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    Environment environment;

    @InjectMocks
    private ConsumirPedidos consumirPedidos;

    @BeforeEach
    public void initMocks(){
        this.openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        this.openMocks.close();
    }


    @Test
    void retornarPedido() {
        // Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", i, "30.90"));
        }
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(1L, produtos, "jose", now(), "CRIADO");
        when(restTemplate.getForEntity(any(String.class),any())).thenReturn(ResponseEntity.ok().body(dadosPedidoDto));

        // Act
        DadosPedidoDto dadosPedidoDtoRes = consumirPedidos.retornarPedido(1L);

        // Assert
        assertEquals(dadosPedidoDto, dadosPedidoDtoRes);
        verify(restTemplate, times(1)).getForEntity(any(String.class), any());

    }

    @Test
    void confirmaPagamento() {
        // Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", 200, "30.90"));
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(1L, produtos, "jose", now(), "CRIADO");
        when(restTemplate.postForEntity(any(String.class), any(String.class), any())).thenReturn(ResponseEntity.ok().body(dadosPedidoDto));


        //Act
        consumirPedidos.confirmaPagamento(2L);

        //Assert
        verify(restTemplate, times(1)).postForEntity(any(String.class),  any(), any());

    }
}