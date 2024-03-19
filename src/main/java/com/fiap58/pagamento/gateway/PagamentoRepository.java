package com.fiap58.pagamento.gateway;

import com.fiap58.pagamento.core.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Pagamento findByQrCode(String qrCode);
    Pagamento findByIdPedido(Long idPedido);
}
