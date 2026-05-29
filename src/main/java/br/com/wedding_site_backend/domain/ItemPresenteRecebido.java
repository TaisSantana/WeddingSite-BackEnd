package br.com.wedding_site_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

// ItemPresenteRecebido.java — linha do carrinho, liga os dois
@Entity
@Table(name = "item_presente_recebido")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPresenteRecebido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "presente_recebido_id", nullable = false)
    @ToString.Exclude
    private PresenteRecebido presenteRecebido;

    // snapshot do item escolhido — qual era o presente do catálogo
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "catalogo_presente_id", nullable = false)
    private CatalogoPresente catalogoPresente;

    // valor no momento da compra — útil se você mudar o preço do catálogo depois
    @Column(name = "valor_pago", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorPago;
}