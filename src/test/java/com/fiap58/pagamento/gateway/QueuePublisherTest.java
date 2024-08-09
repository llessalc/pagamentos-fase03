package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.adapter.PagamentoToPagamentoDto;
import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.PagamentoDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class QueuePublisherTest {

    AutoCloseable openMocks;

    public PagamentoDto pagamentoDto;

    @Value("${events.queue-pagamentos-criados}")
    private String queuePagamentosCriados;

    @Value("${events.queue-pagamentos-confirmados}")
    private String queuePagamentosConfirmados;

    @Value("${events.queue-pagamentos-cancelados}")
    private String queuePagamentosCancelados;

    @Mock
    private SqsTemplate sqsTemplate;

    @InjectMocks
    private QueuePublisher queuePublisher;

    @BeforeEach
    public void initMocks(){
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void closeMock() throws Exception {
        openMocks.close();
    }

    @BeforeEach
    public void setUpPagamento() {
        Pagamento pagamento = new Pagamento(1L);
        BigDecimal valorTotal = new BigDecimal("10");
        pagamento.setValorTotal(valorTotal);
        pagamentoDto = PagamentoToPagamentoDto.pagamentoToPagamentoDto(pagamento);
    }

    @BeforeAll
    public void setUp() {
        ReflectionTestUtils.setField(queuePublisher, "queuePagamentosCriados", queuePagamentosCriados);
        ReflectionTestUtils.setField(queuePublisher, "queuePagamentosConfirmados", queuePagamentosConfirmados);
        ReflectionTestUtils.setField(queuePublisher, "queuePagamentosCancelados", queuePagamentosCancelados);
    }




    @Test
    void publicarPagamento() {
        //Arrange
        String queueName = "testQueue";

        //Act
        queuePublisher.publicarPagamento(pagamentoDto, queueName);

        //Assert
        verify(sqsTemplate, times(1)).send(any());
    }

    @Test
    void publicarPagamentoCriado() {
        //Arrange
        QueuePublisher spyPublisher = spy(queuePublisher);

        //Act
        spyPublisher.publicarPagamentoCriado(pagamentoDto);

        //verify
        verify(spyPublisher, times(1)).publicarPagamento(pagamentoDto,queuePagamentosCriados);
    }

    @Test
    void publicarPagamentoConfirmado() {
        //Arrange
        QueuePublisher spyPublisher = spy(queuePublisher);

        //Act
        spyPublisher.publicarPagamentoConfirmado(pagamentoDto);

        //verify
        verify(spyPublisher, times(1)).publicarPagamento(pagamentoDto,queuePagamentosConfirmados);
    }

    @Test
    void publicarPagamentoCancelado() {
        //Arrange
        QueuePublisher spyPublisher = spy(queuePublisher);

        //Act
        spyPublisher.publicarPagamentoCancelado(pagamentoDto);

        //verify
        verify(spyPublisher, times(1)).publicarPagamento(pagamentoDto,queuePagamentosCancelados);
    }
}