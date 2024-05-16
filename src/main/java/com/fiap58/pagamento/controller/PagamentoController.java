package com.fiap58.pagamento.controller;

import com.fiap58.pagamento.core.usecases.PagamentoUseCases;
import com.fiap58.pagamento.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gerenciamento-pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoUseCases pagamentoUseCases;

    @Operation(description = "Cria pagamento a partir de um pedido.")
    @PostMapping("/criar-pagamento/pedido/{id}")
    public ResponseEntity<PagamentoDto> criarPagamento(@PathVariable long id){
        PagamentoDto pagamento = pagamentoUseCases.criarPagamento(id);
        return ResponseEntity.ok(pagamento);
    }

    @Operation(description = "Listar pagamentos criados.")
    @GetMapping("/listar")
    public ResponseEntity<List<PagamentoDto>> listarPagamentos(@RequestParam(name="limit", required = false, defaultValue = "-1") int limit) {
        List<PagamentoDto> pagamentos = pagamentoUseCases.listarPagamentos(limit);
        return ResponseEntity.ok(pagamentos);
    }

    @Operation(description = "Busca pagamento por id ou Qr Code")
    @GetMapping("/pagamento")
    public ResponseEntity<Optional<PagamentoDto>> buscarPagamento(@RequestParam(name="id", required = false) Long id,
                                                                 @RequestParam(name="qrCode", required = false) String qrCode) {

        if (id != null) {
            return ResponseEntity.ok(pagamentoUseCases.buscarPagamento(id));
        } else if (qrCode != null) {
            return ResponseEntity.ok(pagamentoUseCases.buscarPagamentoPorQrCode(qrCode));
        }
        return ResponseEntity.ok(null);
    }


    @PostMapping("/confirmar-pagamento-wh")
    public ResponseEntity confirmarPagamentoHook(@RequestBody PagamentoWhDto pagamentoWhDto) {
        String pagamentoStatus = pagamentoUseCases.confirmarPagamentoHook(pagamentoWhDto);

        return ResponseEntity.ok(pagamentoStatus);

    }

    @PostMapping("/confirmar-pagamento-manual/{id}")
    public ResponseEntity confirmarPagamentoManual(@PathVariable long id) {
        Boolean pagamentoFoiconfirmado = pagamentoUseCases.confirmarPagamentoManual(id);

        return ResponseEntity.ok(pagamentoFoiconfirmado);

    }

}
