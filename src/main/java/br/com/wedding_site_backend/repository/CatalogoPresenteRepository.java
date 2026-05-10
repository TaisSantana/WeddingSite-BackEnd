package br.com.wedding_site_backend.repository;

import br.com.wedding_site_backend.domain.CatalogoPresente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogoPresenteRepository extends JpaRepository<CatalogoPresente, Long> {
}