package br.com.wedding_site_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// PresenteRecebido.java — o pagamento feito pelo convidado
@Entity
@Table(name = "presente_recebido")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PresenteRecebido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "forma_pagamento", nullable = false)
    private String formaPagamento; // PIX | CARTAO

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "status_pagamento", nullable = false)
    @Builder.Default
    private String statusPagamento = "PENDENTE";

    @Column(name = "mp_payment_id")
    private String mpPaymentId;

    @Column(name = "mp_preference_id")
    private String mpPreferenceId;

    @Column(name = "mp_external_reference")
    private String mpExternalReference;

    @Column(name = "pago_em")
    private LocalDateTime pagoEm;

    @Column(name = "criado_em", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "presenteRecebido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemPresenteRecebido> itens = new ArrayList<>();
}