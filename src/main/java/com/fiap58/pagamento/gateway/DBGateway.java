package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.dto.PagamentoDto;
import com.fiap58.pagamento.interfaces.IDB;
import com.fiap58.pagamento.interfaces.IPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DBGateway implements IDB {

    @Autowired
    private IPagamentoRepository pagamentoRepository;


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

    @Override
    public void excluirPagamento(Long id) {
        Optional<PagamentoDto> pagamento = pagamentoRepository.findById(id);
        if(pagamento.isPresent()){
            PagamentoDto pagamentoDto1 = pagamento.get();
            pagamentoDto1.setDeletadoEm(Instant.now());
            pagamentoRepository.save(pagamentoDto1);
        }
    }

}
