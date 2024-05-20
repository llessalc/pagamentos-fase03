package com.fiap58.pagamento.interfaces;

import com.fiap58.pagamento.dto.PagamentoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<PagamentoDto, Long> {
    Optional<PagamentoDto> findByQrCode(String qrCode);
    Optional<PagamentoDto> findByIdPedido(Long idPedido);

}
