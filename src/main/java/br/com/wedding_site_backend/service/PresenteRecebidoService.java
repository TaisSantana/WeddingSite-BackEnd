package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.PresenteRecebido;
import br.com.wedding_site_backend.dto.ItemPresenteRecebidoDTO;
import br.com.wedding_site_backend.dto.PresenteRecebidoDTO;
import br.com.wedding_site_backend.repository.PresenteRecebidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresenteRecebidoService {

    private final PresenteRecebidoRepository repository;

    @Transactional(readOnly = true)
    public List<PresenteRecebidoDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PresenteRecebidoDTO> listarPagos() {
        return repository.findByStatusPagamento("PAGO")
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public PresenteRecebidoDTO toDTO(PresenteRecebido pr) {
        List<ItemPresenteRecebidoDTO> itens = pr.getItens().stream()
                .map(item -> ItemPresenteRecebidoDTO.builder()
                        .catalogoId(item.getCatalogoPresente().getId())
                        .nomePresente(item.getCatalogoPresente().getNome())
                        .valorPago(item.getValorPago())
                        .build())
                .toList();

        return PresenteRecebidoDTO.builder()
                .id(pr.getId())
                .nome(pr.getNome())
                .email(pr.getEmail())
                .mensagem(pr.getMensagem())
                .formaPagamento(pr.getFormaPagamento())
                .total(pr.getTotal())
                .statusPagamento(pr.getStatusPagamento())
                .criadoEm(pr.getCriadoEm())
                .pagoEm(pr.getPagoEm())
                .itens(itens)
                .build();
    }
}