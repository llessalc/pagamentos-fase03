package com.fiap58.pagamento.controller;


import com.fiap58.pagamento.core.entity.Pagamento;
import com.fiap58.pagamento.dto.DadosPedidoDto;
import com.fiap58.pagamento.dto.QrCodeDto;
import com.fiap58.pagamento.dto.QrCodeWhDto;
import com.fiap58.pagamento.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gerenciamento-pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService service;


    @Operation(description = "Cria pagamento a partir de um Pedido")
    @PostMapping("/criar-pagamento/pedido/{id}")
    public ResponseEntity<Pagamento> criarPagamento(@PathVariable long id){
        Pagamento pagamento = service.criarPagamento(id);
        return ResponseEntity.ok(pagamento);
    }

    @Operation(description = "Confirma pagamento e envia pedido para cozinha")
    @PostMapping("/pagamento/confirma")
    public ResponseEntity<DadosPedidoDto> confirmaPagamento(@RequestBody QrCodeWhDto dto){
        DadosPedidoDto pagamento = service.confirmaPagamento(dto);
        return ResponseEntity.ok(pagamento);
    }

    @Operation(description = "Lista pagamento a partir do Qr Code")
    @PostMapping("/pagamento/listar")
    public ResponseEntity<Pagamento> listarPagamento(@RequestBody QrCodeWhDto dto){
        Pagamento pagamento = service.listaPagamentosPorQrCode(dto);
        return ResponseEntity.ok(pagamento);
    }

    @Operation(description = "Lista pagamento a partir do id do pedido")
    @GetMapping("/pagamento/listar/{id}")
    public ResponseEntity<QrCodeDto> listarPagamento(@PathVariable long id){
        QrCodeDto qrCodeDto = service.listaPagamentosPorId(id);
        return ResponseEntity.ok(qrCodeDto);
    }
}
