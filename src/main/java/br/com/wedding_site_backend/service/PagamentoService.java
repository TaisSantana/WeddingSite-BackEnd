    package br.com.wedding_site_backend.service;

    import br.com.wedding_site_backend.domain.CatalogoPresente;
    import br.com.wedding_site_backend.domain.ItemPresenteRecebido;
    import br.com.wedding_site_backend.domain.PresenteRecebido;
    import br.com.wedding_site_backend.dto.PagamentoRequestDTO;
    import br.com.wedding_site_backend.dto.PagamentoResponseDTO;
    import br.com.wedding_site_backend.dto.PixResponseDTO;
    import br.com.wedding_site_backend.dto.StatusPixDTO;
    import br.com.wedding_site_backend.repository.CatalogoPresenteRepository;
    import br.com.wedding_site_backend.repository.PresenteRecebidoRepository;
    import com.mercadopago.client.payment.PaymentClient;
    import com.mercadopago.client.payment.PaymentCreateRequest;
    import com.mercadopago.client.payment.PaymentPayerRequest;
    import com.mercadopago.client.preference.*;
    import com.mercadopago.core.MPRequestOptions;
    import com.mercadopago.exceptions.MPApiException;
    import com.mercadopago.exceptions.MPException;
    import com.mercadopago.resources.payment.Payment;
    import com.mercadopago.resources.preference.Preference;
    import jakarta.persistence.EntityNotFoundException;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import java.time.OffsetDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class PagamentoService {

        private final PresenteRecebidoRepository presenteRecebidoRepo;
        private final CatalogoPresenteRepository catalogoRepo;
        private final EmailService emailService;
        private static final Long LOOT_MISTERIOSO_CATALOGO_ID = -1L;
        private static final BigDecimal VALOR_LIVRE_MIN = BigDecimal.ONE;
        private static final BigDecimal VALOR_LIVRE_MAX = new BigDecimal("50000");


        @Value("${mercadopago.access-token-pix}")
        private String accessTokenPix;

        @Value("${mercadopago.access-token-cartao}")
        private String accessTokenCartao;

        @Value("${mercadopago.success-url}")
        private String successUrl;

        @Value("${mercadopago.failure-url}")
        private String failureUrl;

        @Value("${mercadopago.pending-url}")
        private String pendingUrl;

        // ══════════════════════════════════════════════════════
        // PIX
        // ══════════════════════════════════════════════════════
        @Transactional
        public PixResponseDTO iniciarPix(PagamentoRequestDTO req) {
            BigDecimal total = calcularTotal(req.getItens());

            try {
                MPRequestOptions requestOptions = MPRequestOptions.builder()
                        .accessToken(accessTokenPix)
                        .customHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString()))
                        .build();

                PaymentCreateRequest pixReq = PaymentCreateRequest.builder()
                        .transactionAmount(total)
                        .description("Presente Casamento Tais e Gabriel")
                        .paymentMethodId("pix")
                        .dateOfExpiration(OffsetDateTime.now().plusMinutes(30))
                        .payer(PaymentPayerRequest.builder()
                                .email(req.getEmail())
                                .firstName(primeiroNome(req.getNome()))
                                .build())
                        .build();

                PaymentClient client = new PaymentClient();
                Payment payment = client.create(pixReq, requestOptions);

                String mpPaymentId = String.valueOf(payment.getId());
                String copiaECola  = payment.getPointOfInteraction().getTransactionData().getQrCode();
                String qrBase64    = payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();

                PresenteRecebido pr = salvarPresenteRecebido(req, total, "PIX", mpPaymentId, null, null);

                return PixResponseDTO.builder()
                        .presenteRecebidoId(pr.getId())
                        .mpPaymentId(mpPaymentId)
                        .valor(total)
                        .copiaECola(copiaECola)
                        .qrCodeBase64(qrBase64)
                        .expiresAt(OffsetDateTime.now().plusMinutes(30).toString())
                        .status("PENDENTE")
                        .build();

            } catch (MPApiException e) {
                log.error("Erro MP API Pix: {} - {}", e.getStatusCode(), e.getApiResponse().getContent());
                throw new RuntimeException("Erro ao gerar Pix: " + e.getApiResponse().getContent());
            } catch (MPException e) {
                log.error("Erro MP Pix: {}", e.getMessage());
                throw new RuntimeException("Erro ao gerar Pix. Tente novamente.");
            }
        }

        // ── Polling de status Pix ─────────────────────────────
        public StatusPixDTO consultarStatusPix(String mpPaymentId) {
            try {
                MPRequestOptions requestOptions = MPRequestOptions.builder()
                        .accessToken(accessTokenPix)
                        .build();

                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(mpPaymentId), requestOptions);
                String status = mapearStatus(payment.getStatus());

                if ("PAGO".equals(status)) {
                    onPagamentoConfirmado(mpPaymentId, null, "PIX");
                } else if ("EXPIRADO".equals(status)) {
                    presenteRecebidoRepo.findByMpPaymentId(mpPaymentId).ifPresent(pr -> {
                        pr.setStatusPagamento("EXPIRADO");
                        presenteRecebidoRepo.save(pr);
                    });
                }

                return StatusPixDTO.builder().paymentId(mpPaymentId).status(status).build();

            } catch (Exception e) {
                log.warn("Erro ao consultar Pix {}: {}", mpPaymentId, e.getMessage());
                return StatusPixDTO.builder().paymentId(mpPaymentId).status("PENDENTE").build();
            }
        }

        // ══════════════════════════════════════════════════════
        // CHECKOUT PRO (cartao)
        // ══════════════════════════════════════════════════════
        @Transactional
        public PagamentoResponseDTO criarCheckoutPro(PagamentoRequestDTO req) {
            BigDecimal total   = calcularTotal(req.getItens());
            String externalRef = "CASAMENTO-" + UUID.randomUUID();

            try {
                MPRequestOptions requestOptions = MPRequestOptions.builder()
                        .accessToken(accessTokenCartao)
                        .customHeaders(Map.of("x-idempotency-key", UUID.randomUUID().toString()))
                        .build();

                List<PreferenceItemRequest> itensPreferencia = req.getItens().stream()
                        .map(item -> {
                            CatalogoPresente cp = catalogoRepo.findById(item.getCatalogoId())
                                    .orElseThrow(() -> new EntityNotFoundException(
                                            "Presente nao encontrado: " + item.getCatalogoId()));
                            return PreferenceItemRequest.builder()
                                    .id(String.valueOf(cp.getId()))
                                    .title(cp.getNome())
                                    .description(cp.getDescricao() != null ? cp.getDescricao() : "")
                                    .quantity(1)
                                    .unitPrice(cp.getValor())
                                    .currencyId("BRL")
                                    .build();
                        })
                        .toList();

                PreferenceRequest preferenceReq = PreferenceRequest.builder()
                        .items(itensPreferencia)
                        .payer(PreferencePayerRequest.builder()
                                .name(req.getNome())
                                .email(req.getEmail())
                                .build())
                        .backUrls(PreferenceBackUrlsRequest.builder()
                                .success(successUrl)
                                .failure(failureUrl)
                                .pending(pendingUrl)
                                .build())
                        .autoReturn("approved")
                        .statementDescriptor("TAIS GABRIEL")
                        .externalReference(externalRef)
                        .build();

                PreferenceClient client = new PreferenceClient();
                Preference preference = client.create(preferenceReq, requestOptions);

                PresenteRecebido pr = salvarPresenteRecebido(
                        req, total, "CARTAO", null, preference.getId(), externalRef);

                return PagamentoResponseDTO.builder()
                        .presenteRecebidoId(pr.getId())
                        .preferenceId(preference.getId())
                        .checkoutUrl(preference.getInitPoint())
                        .sandboxUrl(preference.getSandboxInitPoint())
                        .build();

            } catch (MPApiException e) {
                log.error("Erro MP API Checkout: {} - {}", e.getStatusCode(), e.getApiResponse().getContent());
                throw new RuntimeException("Erro ao criar checkout: " + e.getApiResponse().getContent());
            } catch (MPException e) {
                log.error("Erro MP Checkout: {}", e.getMessage());
                throw new RuntimeException("Erro ao criar checkout. Tente novamente.");
            }
        }

        // ══════════════════════════════════════════════════════
        // WEBHOOK
        // Configure: MP Developer -> sua app -> Webhooks
        // URL: https://sua-api.railway.app/api/pagamentos/webhook
        // Eventos: payment
        // ══════════════════════════════════════════════════════
        @Transactional
        @SuppressWarnings("unchecked")
        public void processarWebhook(Map<String, Object> payload) {
            try {
                String type = (String) payload.get("type");
                if (!"payment".equals(type)) return;

                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data == null) return;

                String mpPaymentId = String.valueOf(data.get("id"));

                MPRequestOptions requestOptions = MPRequestOptions.builder()
                        .accessToken(accessTokenCartao)
                        .build();

                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(mpPaymentId), requestOptions);
                String status = mapearStatus(payment.getStatus());

                log.info("Webhook MP: payment {} -> {}", mpPaymentId, status);

                if ("PAGO".equals(status)) {
                    String forma = "pix".equals(payment.getPaymentMethodId()) ? "PIX" : "CARTAO";
                    onPagamentoConfirmado(mpPaymentId, payment.getExternalReference(), forma);
                }
            } catch (Exception e) {
                log.error("Erro no webhook MP: {}", e.getMessage(), e);
            }
        }

        // ── Confirmacao ───────────────────────────────────────
        private void onPagamentoConfirmado(String mpPaymentId, String externalRef, String forma) {
            PresenteRecebido pr = null;

            if (mpPaymentId != null) {
                pr = presenteRecebidoRepo.findByMpPaymentId(mpPaymentId).orElse(null);
            }
            if (pr == null && externalRef != null) {
                pr = presenteRecebidoRepo.findByMpExternalReference(externalRef).orElse(null);
            }
            if (pr == null) {
                log.warn("PresenteRecebido nao encontrado: paymentId={} ref={}", mpPaymentId, externalRef);
                return;
            }
            if ("PAGO".equals(pr.getStatusPagamento())) return; // idempotencia

            pr.setStatusPagamento("PAGO");
            pr.setPagoEm(LocalDateTime.now());
            if (mpPaymentId != null) pr.setMpPaymentId(mpPaymentId);
            presenteRecebidoRepo.save(pr);

            emailService.enviarConfirmacaoConvidado(pr.getNome(), pr.getEmail(), pr, forma);
            emailService.enviarNotificacaoNoivos(pr.getNome(), pr.getEmail(), pr.getMensagem(), pr, forma);
        }

        // ── Salvar ────────────────────────────────────────────
        private PresenteRecebido salvarPresenteRecebido(
                PagamentoRequestDTO req, BigDecimal total, String forma,
                String mpPaymentId, String mpPreferenceId, String externalRef) {

            PresenteRecebido pr = PresenteRecebido.builder()
                    .nome(req.getNome())
                    .email(req.getEmail())
                    .mensagem(req.getMensagem())
                    .formaPagamento(forma)
                    .total(total)
                    .statusPagamento("PENDENTE")
                    .mpPaymentId(mpPaymentId)
                    .mpPreferenceId(mpPreferenceId)
                    .mpExternalReference(externalRef)
                    .criadoEm(LocalDateTime.now())
                    .build();

            List<ItemPresenteRecebido> itens = new ArrayList<>();
            for (PagamentoRequestDTO.ItemCarrinhoDTO item : req.getItens()) {
                CatalogoPresente cp = catalogoRepo.findById(item.getCatalogoId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Catalogo nao encontrado: " + item.getCatalogoId()));

                BigDecimal valorPago = resolverValorItem(item);
                itens.add(ItemPresenteRecebido.builder()
                        .presenteRecebido(pr)
                        .catalogoPresente(cp)
                        .valorPago(valorPago)
                        .build());
            }

            pr.setItens(itens);
            return presenteRecebidoRepo.save(pr);
        }

        // ── Util ──────────────────────────────────────────────
        private BigDecimal calcularTotal(List<PagamentoRequestDTO.ItemCarrinhoDTO> itens) {
            return itens.stream()
                    .map(this::resolverValorItem)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        private BigDecimal resolverValorItem(PagamentoRequestDTO.ItemCarrinhoDTO item) {
            if (LOOT_MISTERIOSO_CATALOGO_ID.equals(item.getCatalogoId())) {
                return validarValorLivre(item.getValorPersonalizado());
            }
            return catalogoRepo.findById(item.getCatalogoId())
                    .map(CatalogoPresente::getValor)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Presente nao encontrado: " + item.getCatalogoId()));
        }

        private BigDecimal validarValorLivre(BigDecimal valor) {
            if (valor == null || valor.compareTo(VALOR_LIVRE_MIN) < 0) {
                throw new IllegalArgumentException("Informe um valor válido para o Loot Misterioso da Guilda.");
            }
            if (valor.compareTo(VALOR_LIVRE_MAX) > 0) {
                throw new IllegalArgumentException("Valor acima do permitido para essa contribuição.");
            }
            return valor.setScale(2, java.math.RoundingMode.HALF_UP);
        }


        private String mapearStatus(String mpStatus) {
            if (mpStatus == null) return "PENDENTE";
            return switch (mpStatus) {
                case "approved"              -> "PAGO";
                case "rejected", "cancelled" -> "EXPIRADO";
                default                      -> "PENDENTE";
            };
        }

        private String primeiroNome(String nome) {
            String[] p = nome.trim().split("\\s+");
            return p[0];
        }
    }