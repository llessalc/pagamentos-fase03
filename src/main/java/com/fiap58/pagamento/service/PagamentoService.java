package com.fiap58.pagamento.service;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.ProdutoCarrinhoSaida;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.dto.QrCodeWhDto;
import com.fiap58.pagamento.gateway.ConsumerApiMP;
import com.fiap58.pagamento.gateway.PagamentoRepository;
import com.fiap58.pagamento.gateway.impl.ConsumirPedidos;
import com.fiap58.pagamento.gateway.impl.ImplConsumerApiMP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private ConsumirPedidos consumirPedidos;

    @Autowired
    private ImplConsumerApiMP consumerApiMP;

    public Pagamento criarPagamento(long id) {
        DadosPedidoDto dadosPedidoDto = consumirPedidos.retornarPedido(id);
        BigDecimal valorPedido = calculaValorPedido(dadosPedidoDto);

        Pagamento pagamento = new Pagamento(id, valorPedido);
        QrCodeDto qrCodeDto = consumerApiMP.retornaQrCode(pagamento);
        atualizaPagamento(pagamento, qrCodeDto);


        return repository.save(pagamento);
    }

    private BigDecimal calculaValorPedido(DadosPedidoDto dto){
        BigDecimal valorTotalPedido = new BigDecimal("0");
        List<ProdutoCarrinhoSaida> produtos = dto.produtos();
        for(ProdutoCarrinhoSaida produto : produtos){
            BigDecimal valorUnitario = new BigDecimal(produto.precoAtual());

            valorTotalPedido = valorTotalPedido.add(valorUnitario.multiply(new BigDecimal(produto.quantidade())));
        }
        return valorTotalPedido;
    }

    private void atualizaPagamento(Pagamento pagamento, QrCodeDto qrCodeDto){
        pagamento.setQrCode(qrCodeDto.qr_data());
        pagamento.setInStoreOrderId(qrCodeDto.in_store_order_id());
        pagamento.setAtualizadoEm(Instant.now());
    }

    public Pagamento listaPagamentosPorQrCode(QrCodeWhDto qrCodeWhDto){
        return repository.findByQrCode(qrCodeWhDto.qr_code());
    };

    public DadosPedidoDto confirmaPagamento(QrCodeWhDto qrCodeWhDto) {
        Pagamento pagamento = listaPagamentosPorQrCode(qrCodeWhDto);


        DadosPedidoDto dadosPedidoDto = consumirPedidos.confirmaPagamento(pagamento.getIdPedido());
        return dadosPedidoDto;
    }

    public QrCodeDto listaPagamentosPorId(long idPedido) {
        Pagamento pagamento = repository.findByIdPedido(idPedido);
        return new QrCodeDto(pagamento.getInStoreOrderId(), pagamento.getQrCode());
    }
}
