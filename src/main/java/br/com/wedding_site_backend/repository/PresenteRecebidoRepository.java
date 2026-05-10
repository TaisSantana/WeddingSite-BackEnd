package br.com.wedding_site_backend.repository;

import br.com.wedding_site_backend.domain.PresenteRecebido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresenteRecebidoRepository extends JpaRepository<PresenteRecebido, Long> {

    // Busca pelo ID de pagamento Pix retornado pelo MP
    Optional<PresenteRecebido> findByMpPaymentId(String mpPaymentId);

    // Busca pela referência externa — usada no webhook do Checkout Pro (cartão)
    Optional<PresenteRecebido> findByMpExternalReference(String mpExternalReference);

    // Lista apenas os pagamentos confirmados
    List<PresenteRecebido> findByStatusPagamento(String statusPagamento);

    // Soma total arrecadado de pagamentos PAGO
    @Query("SELECT COALESCE(SUM(p.total), 0) FROM PresenteRecebido p WHERE p.statusPagamento = 'PAGO'")
    BigDecimal somarTotalPago();
}
