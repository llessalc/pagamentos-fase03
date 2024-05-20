package com.fiap58.pagamento.core.usecases;

import com.fiap58.pagamento.adapter.PagamentoToPagamentoDto;
import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.core.entity.StatusPagamento;
import com.fiap58.pagamento.dto.*;
import com.fiap58.pagamento.gateway.ConsumirPedidos;
import com.fiap58.pagamento.gateway.DBGateway;
import com.fiap58.pagamento.gateway.ImplConsumerApiMP;
import com.fiap58.pagamento.mocks.ConsumerApiMPMock;
import com.fiap58.pagamento.mocks.ConsumerPedidosMock;
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
    private ConsumirPedidos consumerPedidos;

    @Autowired
    private ImplConsumerApiMP consumerApiMP;


    public PagamentoDto criarPagamento(long id) {
        DadosPedidoDto dadosPedidoDto = consumerPedidos.retornarPedido(id);
        BigDecimal valorPedido = calculaValorPedido(dadosPedidoDto);
        Pagamento pagamento = new Pagamento(id);
        pagamento.setValorTotal(valorPedido);

        QrCodeDto qrCode = consumerApiMP.retornaQrCode(pagamento);

        pagamento.setQrCode(qrCode.qr_data());
        pagamento.setInStoreOrderId(qrCode.in_store_order_id());

        PagamentoDto pagamentoDto = PagamentoToPagamentoDto.pagamentoToPagamentoDto(pagamento);

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

    public String confirmarPagamentoHook(PagamentoWhDto pagamentoWhDto) {
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
                    System.out.println("Pedido pago!");
                    pagamentoDto.setStatus(StatusPagamento.PAGO);
                    dbGateway.savePagamento(pagamentoDto);

                    //Confirmar ao serviço de pedidos que o Pagamento foi realizado
                    consumerPedidos.confirmaPagamento(pagamentoId);

                } else if (pagamentoStatus.equals("expired")) {
                    System.out.println("Pedido cancelado!");
                    pagamentoDto.setStatus(StatusPagamento.CANCELADO);
                    dbGateway.savePagamento(pagamentoDto);
                }

            }

            return pagamentoStatus;

        } else {

            return "Not a merchant order notification";
        }
    }

    public Boolean confirmarPagamentoManual(long id) {
        Optional<PagamentoDto> pagamentoDtoOpt = this.buscarPagamento(id);
        if (pagamentoDtoOpt.isPresent()) {
            PagamentoDto pagamentoDto = pagamentoDtoOpt.get();

            pagamentoDto.setStatus(StatusPagamento.PAGO);

            dbGateway.savePagamento(pagamentoDto);

            return Boolean.TRUE;

        }

        return Boolean.FALSE;
    }

    public Boolean checkOrderNotification(String url) {
        return (url != null && url.contains("api.mercadolibre.com/merchant_orders"));
    }


}
