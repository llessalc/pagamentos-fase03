package com.fiap58.pagamento.core.usecases;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.core.entity.StatusPagamento;
import com.fiap58.pagamento.dto.*;
import com.fiap58.pagamento.gateway.*;
import com.fiap58.pagamento.mocks.ConsumerApiMPMock;
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
    QueuePublisher queuePublisher;

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
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(1L, produtos, "jose", now(), "CRIADO");

        when(consumerApiMP.retornaQrCode(any(Pagamento.class))).thenReturn(new QrCodeDto("aa", expectedQrCode));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        PagamentoDto resPagamentoDto = pagamentoUseCases.criarPagamento(dadosPedidoDto);

        // Assert
        assertEquals(new BigDecimal(expectedPagamento), resPagamentoDto.getValorTotal());
        assertEquals(expectedQrCode, resPagamentoDto.getQrCode());

        verify(queuePublisher, times(1)).publicarPagamentoCriado(any(PagamentoDto.class));
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
        DadosPedidoDto dadosPedidoDto = new DadosPedidoDto(1L, produtos, "jose", now(), "CRIADO");

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
        pagamentoUseCases.handlePagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(StatusPagamento.PAGO, spyPagamentoDto.getStatus());

        verify(dbGateway, times(1)).savePagamento(any());
        verify(queuePublisher, times(1)).publicarPagamentoConfirmado(any(PagamentoDto.class));

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
        pagamentoUseCases.handlePagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(StatusPagamento.CANCELADO, spyPagamentoDto.getStatus());

        verify(dbGateway, times(1)).savePagamento(any());
        verify(queuePublisher, times(1)).publicarPagamentoCancelado(any(PagamentoDto.class));

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
        String statusRes = pagamentoUseCases.handlePagamentoHook(pagamentoWhDto);

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
        String statusRes = pagamentoUseCases.handlePagamentoHook(pagamentoWhDto);

        // Assert
        assertEquals(status, statusRes);
        verify(dbGateway, never()).savePagamento(any());
        verify(queuePublisher, never()).publicarPagamentoCriado(any());
        verify(queuePublisher, never()).publicarPagamentoCancelado(any());
        verify(queuePublisher, never()).publicarPagamentoConfirmado(any());

    }

    @Test
    void confirmarPagamentoHook_notOrder() {
        // Arrange
        String whResource = "api.mercadolibre.com/anything";
        String whTopic = "merchant_orders";
        String expectedStatus = "Not a merchant order notification";

        PagamentoWhDto pagamentoWhDto = new PagamentoWhDto(whResource, whTopic);

        // Act
        String pagamentoStatus = pagamentoUseCases.handlePagamentoHook(pagamentoWhDto);

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
        verify(queuePublisher, times(1)).publicarPagamentoConfirmado(any());

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
    void cancelarPagamentoManual() {
        // Arrange
        long idPedido = 1L;
        PagamentoDto pagamentoDto = new PagamentoDto(idPedido, new BigDecimal("10"));

        when(dbGateway.buscarPagamento(any(long.class))).thenReturn(Optional.of(pagamentoDto));
        when(dbGateway.savePagamento(any(PagamentoDto.class))).then(AdditionalAnswers.returnsFirstArg());

        // Act
        Boolean confirmation = pagamentoUseCases.cancelarPagamentoManual(idPedido);

        // Assert
        assertEquals(Boolean.TRUE, confirmation);
        verify(queuePublisher, times(1)).publicarPagamentoCancelado(any());
    }

    @Test
    void cancelarPagamentoManual_PagamentoNotFound() {
        // Arrange
        Long pagamentoId = 1L;

        // Act
        Boolean confirmation = pagamentoUseCases.confirmarPagamentoManual(pagamentoId);

        // Assert
        assertEquals(Boolean.FALSE, confirmation);
        verify(dbGateway, never()).savePagamento(any());
        verify(queuePublisher, never()).publicarPagamentoCancelado(any());

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


    @Test
    void confirmarPagamento() {
        //Arrange
        long idPedido = 1L;
        PagamentoDto pagamentoDto = new PagamentoDto(idPedido, new BigDecimal("10"));
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        //Act

        pagamentoUseCases.confirmarPagamento(spyPagamentoDto);

        //Assert
        assertEquals(StatusPagamento.PAGO, spyPagamentoDto.getStatus());
        verify(queuePublisher, times(1)).publicarPagamentoConfirmado(any());
        verify(dbGateway, times(1)).savePagamento(any());
    }

    @Test
    void cancelarPagamento() {
        //Arrange
        long idPedido = 1L;
        PagamentoDto pagamentoDto = new PagamentoDto(idPedido, new BigDecimal("10"));
        PagamentoDto spyPagamentoDto = spy(pagamentoDto);

        //Act

        pagamentoUseCases.cancelarPagamento(spyPagamentoDto);

        //Assert
        assertEquals(StatusPagamento.CANCELADO, spyPagamentoDto.getStatus());
        verify(queuePublisher, times(1)).publicarPagamentoCancelado(any());
        verify(dbGateway, times(1)).savePagamento(any());
    }
}


