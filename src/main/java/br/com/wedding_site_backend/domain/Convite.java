package br.com.wedding_site_backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "convites")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Convite {

    @Id
    @Column(nullable = false, length = 5)
    private String codigo;

    @Column(nullable = false)
    private String familia;

    @OneToMany(mappedBy = "convite", cascade = CascadeType.ALL,
               fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<Convidado> convidados = new ArrayList<>();
}