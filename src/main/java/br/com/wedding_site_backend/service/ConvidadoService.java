package br.com.wedding_site_backend.service;

import br.com.wedding_site_backend.domain.Convidado;
import br.com.wedding_site_backend.domain.Convite;
import br.com.wedding_site_backend.dto.ConvidadoDTO;
import br.com.wedding_site_backend.enums.StatusPresenca;
import br.com.wedding_site_backend.repository.ConvidadoRepository;
import br.com.wedding_site_backend.repository.ConviteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConvidadoService {

    private final ConvidadoRepository repository;
    private final ConviteRepository conviteRepo;

    @Transactional
    public ConvidadoDTO criarConvidado(ConvidadoDTO dto) {

        Convite convite = conviteRepo.findByCodigo(dto.getCodigoConvite())
                .orElseThrow(() ->
                        new EntityNotFoundException("Convite não encontrado"));

        Convidado convidado = Convidado.builder()
                .convite(convite)
                .nome(dto.getNome())
                .status(StatusPresenca.PENDENTE)
                .build();

        return toDTO(repository.save(convidado));
    }

    private ConvidadoDTO toDTO(Convidado convidado) {

        return ConvidadoDTO.builder()
                .id(convidado.getId())
                .nome(convidado.getNome())
                .status(convidado.getStatus())
                .codigoConvite(convidado.getConvite().getCodigo())
                .build();
    }

    @Transactional
    public void atualizarConvidado(Long id, ConvidadoDTO dto) {

        Convidado convidado = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Convidado não encontrado"));

        convidado.setNome(dto.getNome());
        convidado.setStatus(dto.getStatus());

        if (dto.getCodigoConvite() != null) {

            Convite convite = conviteRepo.findByCodigo(dto.getCodigoConvite())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Convite não encontrado"));

            convidado.setConvite(convite);
        }

        repository.save(convidado);
    }

    @Transactional
    public void deletarConvidado(Long id) {

        Convidado convidado = repository.findById(id)
            .orElseThrow(() ->
                new EntityNotFoundException("Convidado não encontrado"));

        repository.delete(convidado);
    }
}