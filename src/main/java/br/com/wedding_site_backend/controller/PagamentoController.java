package br.com.wedding_site_backend.controller;

import br.com.wedding_site_backend.dto.*;
import br.com.wedding_site_backend.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    @Autowired
    private final PagamentoService service;

    /** Gera Pix copia e cola com o valor exato do carrinho */
    @PostMapping("/pix")
    public ResponseEntity<PixResponseDTO> iniciarPix(
            @RequestBody @Valid PagamentoRequestDTO req) {
        return ResponseEntity.ok(service.iniciarPix(req));
    }

    /** Polling — frontend consulta a cada 5s se o Pix foi pago */
    @GetMapping("/pix/{paymentId}/status")
    public ResponseEntity<StatusPixDTO> statusPix(@PathVariable String paymentId) {
        return ResponseEntity.ok(service.consultarStatusPix(paymentId));
    }

    /**
     * Cria preferência Checkout Pro e retorna a URL do ambiente seguro do MP.
     * O Angular redireciona o usuário para essa URL.
     */
    @PostMapping("/checkout")
    public ResponseEntity<PagamentoResponseDTO> criarCheckout(
            @RequestBody @Valid PagamentoRequestDTO req) {
        return ResponseEntity.ok(service.criarCheckoutPro(req));
    }

    /**
     * Webhook Mercado Pago — notificação automática de pagamentos.
     * Configure no painel MP Developer → Sua aplicação → Webhooks:
     *   URL: https://sua-api.railway.app/api/pagamentos/webhook
     *   Eventos: payment
     */
    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody Map<String, Object> payload) {
        service.processarWebhook(payload);
        return ResponseEntity.ok().build();
    }
}