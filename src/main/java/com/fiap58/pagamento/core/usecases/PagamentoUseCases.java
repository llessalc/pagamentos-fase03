package com.fiap58.pagamento.core.usecases;

import com.fiap58.pagamento.adapter.PagamentoToPagamentoDto;
import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.core.entity.StatusPagamento;
import com.fiap58.pagamento.dto.*;
import com.fiap58.pagamento.gateway.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoUseCases {

    @Autowired
    private DBGateway dbGateway;

    @Autowired
    private ImplConsumerApiMP consumerApiMP;

    @Autowired
    private QueuePublisher queuePublisher;

    @Transactional
    public PagamentoDto criarPagamento(DadosPedidoDto dadosPedidoDto) {
        BigDecimal valorPedido = calculaValorPedido(dadosPedidoDto);
        Long pedidoId = dadosPedidoDto.id();
        Pagamento pagamento = new Pagamento(pedidoId);
        pagamento.setValorTotal(valorPedido);

        QrCodeDto qrCode = consumerApiMP.retornaQrCode(pagamento);

        pagamento.setQrCode(qrCode.qr_data());
        pagamento.setInStoreOrderId(qrCode.in_store_order_id());

        PagamentoDto pagamentoDto = PagamentoToPagamentoDto.pagamentoToPagamentoDto(pagamento);

        //Salva novo pagamento na queue de Pagamentos
        queuePublisher.publicarPagamentoCriado(pagamentoDto);

        //Salva novo pagamento no DB
        return dbGateway.savePagamento(pagamentoDto);

    }

    public BigDecimal calculaValorPedido(DadosPedidoDto dto) {
        BigDecimal valorTotalPedido = new BigDecimal("0");
        List<ProdutoCarrinhoSaidaDto> produtos = dto.produtos();
        for (ProdutoCarrinhoSaidaDto produto : produtos) {
            BigDecimal valorUnitario = new BigDecimal(produto.precoAtual());

            valorTotalPedido = valorTotalPedido.add(valorUnitario.multiply(new BigDecimal(produto.quantidade())));
        }

        return valorTotalPedido;
    }

    public List<PagamentoDto> listarPagamentos(int limit) {
        return dbGateway.listarPagamentos(limit);
    }

    public Optional<PagamentoDto> buscarPagamento(long id) {
        return dbGateway.buscarPagamento(id);
    }

    public Optional<PagamentoDto> buscarPagamentoPorQrCode(String qrCode) {
        return dbGateway.buscarPagamentoPorQrCode(qrCode);
    }

    public Optional<PagamentoDto> buscarPagamentoPorIdPedido(long id) {
        return dbGateway.buscarPagamentoPorIdPedido(id);
    }

    @Transactional
    public String handlePagamentoHook(PagamentoWhDto pagamentoWhDto) {
        //Status pode ser opened, closed, expired. A confirmação
        // se dá com status closed

        String pagamentoUrl = pagamentoWhDto.resource();

        if (this.checkOrderNotification(pagamentoUrl)) {

            PagamentoWhStatusDto pagamentoWhStatusDto = consumerApiMP.retornaPagamentoStatus(pagamentoUrl);
            String pagamentoStatus = pagamentoWhStatusDto.status();
            long pagamentoId = pagamentoWhStatusDto.external_reference();

            Optional<PagamentoDto> pagamentoDtoOpt = this.buscarPagamentoPorIdPedido(pagamentoId);

            if (pagamentoDtoOpt.isPresent()) {

                PagamentoDto pagamentoDto = pagamentoDtoOpt.get();

                if (pagamentoStatus.equals("closed")) {
                    this.confirmarPagamento(pagamentoDto);

                } else if (pagamentoStatus.equals("expired")) {
                    this.cancelarPagamento(pagamentoDto);
                }

            }

            return pagamentoStatus;

        } else {

            return "Not a merchant order notification";
        }
    }

    public void confirmarPagamento(PagamentoDto pagamentoDto) {
        System.out.println("Pedido pago!");
        pagamentoDto.setStatus(StatusPagamento.PAGO);

        //Confirmar ao serviço de pedidos que o Pagamento foi realizado
        queuePublisher.publicarPagamentoConfirmado(pagamentoDto);

        dbGateway.savePagamento(pagamentoDto);

    }

    public void cancelarPagamento(PagamentoDto pagamentoDto) {
        System.out.println("Pedido cancelado!");
        pagamentoDto.setStatus(StatusPagamento.CANCELADO);

        // Sinalizar ao serviço de pedidos que o pagamento foi cancelado
        queuePublisher.publicarPagamentoCancelado(pagamentoDto);

        dbGateway.savePagamento(pagamentoDto);
    }

    @Transactional
    public Boolean confirmarPagamentoManual(long id) {
        Optional<PagamentoDto> pagamentoDtoOpt = this.buscarPagamento(id);
        if (pagamentoDtoOpt.isPresent()) {
            PagamentoDto pagamentoDto = pagamentoDtoOpt.get();

            this.confirmarPagamento(pagamentoDto);

            return Boolean.TRUE;

        }

        return Boolean.FALSE;
    }

    @Transactional
    public Boolean cancelarPagamentoManual(long id) {
        Optional<PagamentoDto> pagamentoDtoOpt = this.buscarPagamento(id);
        if (pagamentoDtoOpt.isPresent()) {
            PagamentoDto pagamentoDto = pagamentoDtoOpt.get();

            this.cancelarPagamento(pagamentoDto);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    public Boolean checkOrderNotification(String url) {
        return (url != null && url.contains("api.mercadolibre.com/merchant_orders"));
    }


}
