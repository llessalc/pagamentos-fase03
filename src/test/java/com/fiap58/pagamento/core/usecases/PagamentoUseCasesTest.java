package com.fiap58.pagamento.core.usecases;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.core.entity.StatusPagamento;
import com.fiap58.pagamento.dto.*;
import com.fiap58.pagamento.gateway.ConsumirPedidos;
import com.fiap58.pagamento.gateway.DBGateway;
import com.fiap58.pagamento.gateway.ImplConsumerApiMP;
import com.fiap58.pagamento.mocks.ConsumerApiMPMock;
import com.fiap58.pagamento.mocks.ConsumerPedidosMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PagamentoUseCasesTest {

    AutoCloseable openMocks;

    @Mock
    DBGateway dbGateway;
    @Mock
    ImplConsumerApiMP consumerApiMP;
    @Mock
    ConsumirPedidos consumerPedidos;

    @InjectMocks
    PagamentoUseCases pagamentoUseCases;


    @BeforeEach
    public void initMocks(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        openMocks.close();
    }


    @Test
    void criarPagamento() {
        //Arrange
        Long idPedido = 3L;

        String expectedPagamento = "30.90";
        String expectedQrCode = "bb";

        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", 1, expectedPagamento));
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(produtos, "jose", now(), "CRIADO");

        when(consumerApiMP.retornaQrCode(any(Pagamento.class))).thenReturn(new QrCodeDto("aa", expectedQrCode));
        when(consumerPedidos.retornarPedido(any(Long.class))).thenReturn(dadosPedidoDto);
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        PagamentoDto resPagamentoDto = pagamentoUseCases.criarPagamento(idPedido);

        // Assert
        assertEquals(new BigDecimal(expectedPagamento), resPagamentoDto.getValorTotal());
        assertEquals(expectedQrCode, resPagamentoDto.getQrCode());

        verify(consumerPedidos, times(1)).retornarPedido(any(Long.class));
        verify(consumerApiMP, times(1)).retornaQrCode(any());
        verify(dbGateway, times(1)).savePagamento(any());


    }

    @DisplayName("Teste calculaValorPedido : ")
    @Test
    void calculaValorPedido() {
        // Arrange
        List<ProdutoCarrinhoSaidaDto> produtos = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            produtos.add(new ProdutoCarrinhoSaidaDto("hamburg", i, "11.97"));
        }
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(produtos, "jose", now(), "CRIADO");

        BigDecimal expectedValorPedido = new BigDecimal("35.91");

        //Act
        BigDecimal valorPedido = pagamentoUseCases.calculaValorPedido(dadosPedidoDto);

        //Assert
        assertEquals(expectedValorPedido, valorPedido);

    }

    @Test
    void listarPagamentos() {
        pagamentoUseCases.listarPagamentos(1);
        verify(dbGateway, times(1)).listarPagamentos(any(int.class));
    }

    @Test
    void buscarPagamento() {
        pagamentoUseCases.buscarPagamento(1L);
        verify(dbGateway, times(1)).buscarPagamento(any(Long.class));
    }

    @Test
    void buscarPagamentoPorQrCode() {
        pagamentoUseCases.buscarPagamentoPorQrCode("x");
        verify(dbGateway, times(1)).buscarPagamentoPorQrCode(any(String.class));
    }

    @Test
    void buscarPagamentoPorIdPedido() {
        pagamentoUseCases.buscarPagamentoPorIdPedido(1L);
        verify(dbGateway, times(1)).buscarPagamentoPorIdPedido(any(Long.class));
    }

    @Test
    void confirmarPagamentoHook_Pago() {
        // Arrange
        long externalReference = 1L;
        BigDecimal valorTotal = new BigDecimal("22");
        String status = "closed";
        String whResource = "api.mercadolibre.com/merchant_orders";
        String whTopic = "merchant_orders";

        PagamentoDto pagamentoDto = new PagamentoDto(externalReference, valorTotal);
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        PagamentoWhStatusDto pagamentoWhStatusDto = new PagamentoWhStatusDto(status, externalReference);

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);


        when(consumerApiMP.retornaPagamentoStatus(any(String.class))).thenReturn(pagamentoWhStatusDto);
        when(dbGateway.buscarPagamentoPorIdPedido(any(Long.class))).thenReturn(Optional.of(spyPagamentoDto));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(StatusPagamento.PAGO, spyPagamentoDto.getStatus());

    }

    @Test
    void confirmarPagamentoHook_Cancelado() {
        // Arrange
        long externalReference = 1L;
        BigDecimal valorTotal = new BigDecimal("22");
        String status = "expired";
        String whResource = "api.mercadolibre.com/merchant_orders";
        String whTopic = "merchant_orders";

        PagamentoDto pagamentoDto = new PagamentoDto(externalReference, valorTotal);
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        PagamentoWhStatusDto pagamentoWhStatusDto = new PagamentoWhStatusDto(status, externalReference);

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);


        when(consumerApiMP.retornaPagamentoStatus(any(String.class))).thenReturn(pagamentoWhStatusDto);
        when(dbGateway.buscarPagamentoPorIdPedido(any(Long.class))).thenReturn(Optional.of(spyPagamentoDto));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(StatusPagamento.CANCELADO, spyPagamentoDto.getStatus());

    }

    @Test
    void confirmarPagamentoHook_UnexpectedStatus() {
        // Arrange
        long externalReference = 1L;
        BigDecimal valorTotal = new BigDecimal("22");
        String status = "unexpected";
        String whResource = "api.mercadolibre.com/merchant_orders";
        String whTopic = "merchant_orders";

        PagamentoDto pagamentoDto = new PagamentoDto(externalReference, valorTotal);
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        PagamentoWhStatusDto pagamentoWhStatusDto = new PagamentoWhStatusDto(status, externalReference);

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);


        when(consumerApiMP.retornaPagamentoStatus(any(String.class))).thenReturn(pagamentoWhStatusDto);
        when(dbGateway.buscarPagamentoPorIdPedido(any(Long.class))).thenReturn(Optional.of(spyPagamentoDto));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        String statusRes = pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(StatusPagamento.CRIADO, spyPagamentoDto.getStatus());
        assertEquals(status, statusRes);

    }

    @Test
    void confirmarPagamentoHook_PagamentoNotFound() {
        // Arrange
        long externalReference = 1L;
        BigDecimal valorTotal = new BigDecimal("22");
        String status = "unexpected";
        String whResource = "api.mercadolibre.com/merchant_orders";
        String whTopic = "merchant_orders";

        PagamentoDto pagamentoDto = new PagamentoDto(externalReference, valorTotal);
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        PagamentoWhStatusDto pagamentoWhStatusDto = new PagamentoWhStatusDto(status, externalReference);

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);

        when(consumerApiMP.retornaPagamentoStatus(any(String.class))).thenReturn(pagamentoWhStatusDto);

        // Act
        String statusRes = pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(status, statusRes);
        verify(dbGateway, never()).savePagamento(any());

    }

    @Test
    void confirmarPagamentoHook_notOrder() {
        // Arrange
        String whResource = "api.mercadolibre.com/anything";
        String whTopic = "merchant_orders";
        String expectedStatus = "Not a merchant order notification";

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);

        // Act
        String pagamentoStatus = pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(expectedStatus, pagamentoStatus);

    }

    @Test
    void confirmarPagamentoManual() {
        // Arrange
        long idPedido = 1L;
        PagamentoDto pagamentoDto = new PagamentoDto(idPedido, new BigDecimal("10"));

        when(dbGateway.buscarPagamento(any(long.class))).thenReturn(Optional.of(pagamentoDto));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        Boolean confirmation = pagamentoUseCases.confirmarPagamentoManual(idPedido);

        // Assert
        assertEquals(Boolean.TRUE, confirmation);

    }


    @Test
    void confirmarPagamentoManual_PagamentoNotFound() {
        // Arrange
        Long pagamentoId = 1L;

        // Act
        Boolean confirmation = pagamentoUseCases.confirmarPagamentoManual(pagamentoId);

        // Assert
        assertEquals(Boolean.FALSE, confirmation);
        verify(dbGateway, never()).savePagamento(any());

    }

    @Test
    void checkOrderNotification() {
        //Arrange
        String url = "https://api.mercadolibre.com/merchant_orders/1234";

        //Act
        Boolean isOrderNotification = pagamentoUseCases.checkOrderNotification(url);

        //Assert
        assertEquals(Boolean.TRUE, isOrderNotification);
    }

    @Test
    void checkOrderNotificationFalse() {
        //Arrange
        String url = "https://api.mercadolibre.com/churrascoefarofagrossa/1234";

        //Act
        Boolean isOrderNotification = pagamentoUseCases.checkOrderNotification(url);

        //Assert
        assertEquals(Boolean.FALSE, isOrderNotification);
    }

    @Test
    void checkOrderNotificationFalse_NullUrl() {
        //Act
        Boolean isOrderNotification = pagamentoUseCases.checkOrderNotification(null);

        //Assert
        assertEquals(Boolean.FALSE, isOrderNotification);
    }
}


