package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.ItemDto;
import com.fiap58.pagamento.dto.PagamentoWhStatusDto;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.dto.QrCodeReq;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ImplConsumerApiMPTest {

    AutoCloseable openMocks;

    @Mock
    private Environment environment;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ImplConsumerApiMP implConsumerApiMP;

    @BeforeEach
    public void initMocks(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        openMocks.close();
    }

    @Test
    void retornaQrCode() {
        // Arrange
        Pagamento pagamento = new Pagamento(1L);
        QrCodeDto qrCodeDto = new QrCodeDto("xx", "zzz");
        when(environment.getProperty(any())).thenReturn("xxxxx");
        when(restTemplate.postForEntity(any(String.class), any(), any())).thenReturn(ResponseEntity.ok().body(qrCodeDto));

        // Act
        QrCodeDto qrCodeDto1 = implConsumerApiMP.retornaQrCode(pagamento);
        verify(restTemplate, times(1)).postForEntity(any(String.class), any(), any());
    }

    @Test
    void retornaPagamentoStatus() {

        PagamentoWhStatusDto pagamentoWhStatusDto = new PagamentoWhStatusDto("closed",  1L);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), ArgumentMatchers.<Class<PagamentoWhStatusDto>>any())).thenReturn(ResponseEntity.ok().body(pagamentoWhStatusDto));

        PagamentoWhStatusDto pagamentoWhStatusDto1 = implConsumerApiMP.retornaPagamentoStatus("xxx");
        assertEquals(pagamentoWhStatusDto1.status(), pagamentoWhStatusDto.status());

    }
}