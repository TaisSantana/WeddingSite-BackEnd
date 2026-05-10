package br.com.wedding_site_backend.repository;

import br.com.wedding_site_backend.domain.Convite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConviteRepository extends JpaRepository<Convite, String> {
    Optional<Convite> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}