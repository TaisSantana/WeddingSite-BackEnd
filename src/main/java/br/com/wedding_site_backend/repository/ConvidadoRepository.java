package br.com.wedding_site_backend.repository;

import br.com.wedding_site_backend.domain.Convidado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConvidadoRepository extends JpaRepository<Convidado, Long> {
    List<Convidado> findByStatus(String status);
    long countByStatus(String status);
}