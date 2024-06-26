package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.PagamentoDto;
import com.fiap58.pagamento.interfaces.DBInterface;
import com.fiap58.pagamento.interfaces.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DBGateway implements DBInterface {

    @Autowired
    private PagamentoRepository pagamentoRepository;


    @Override
    public PagamentoDto savePagamento(PagamentoDto pagamentoDto) {
        return pagamentoRepository.save(pagamentoDto);

    }


    @Override
    public List<PagamentoDto> listarPagamentos(int limit) {
        if (limit > 0) {
            PageRequest pageLimit = PageRequest.of(0,limit);
            return pagamentoRepository.findAll(pageLimit).getContent();
        } else {
            return pagamentoRepository.findAll();
        }
    }

    @Override
    public Optional<PagamentoDto> buscarPagamento(long id) {
        return pagamentoRepository.findById(id);
    }

    @Override
    public Optional<PagamentoDto> buscarPagamentoPorQrCode(String qrCode) {
        return pagamentoRepository.findByQrCode(qrCode);
    }

    @Override
    public Optional<PagamentoDto> buscarPagamentoPorIdPedido(long id) {return pagamentoRepository.findByIdPedido(id);}

}
