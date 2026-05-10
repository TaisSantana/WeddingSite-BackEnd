package br.com.wedding_site_backend.domain;

import br.com.wedding_site_backend.enums.StatusPresenca;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "convidados")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Convidado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convite_codigo", nullable = false)
    @ToString.Exclude
    private Convite convite;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusPresenca status = StatusPresenca.PENDENTE;
}