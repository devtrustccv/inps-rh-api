package cv.inps.rh.configuracao.application.service;

import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VinculoMovimentoService {

  private final ParamVinculoMovimentoEntityRepository repository;
  private final ParamVinculoEntityRepository vinculoRepository;
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public List<VinculoMovimentoResponseDTO> listarPorVinculo(String vinculoId) {
    var vinculo = vinculoRepository.findByUuidOrThrow(UUID.fromString(vinculoId));
    return repository.findByVinculoId_IdAndEstadoNot(vinculo.getId(), Estado.E).stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public List<VinculoMovimentoResponseDTO> syncMovimentos(String vinculoId, List<VinculoMovimentoRequestDTO> items) {
    var vinculo = vinculoRepository.findByUuidOrThrow(UUID.fromString(vinculoId));
    var existingList = repository.findByVinculoId_IdAndEstadoNot(vinculo.getId(), Estado.E);

    if (items == null) items = List.of();

    for (var dto : items) {
      ParamVinculoMovimentoEntity found = null;
      if (dto.getId() != null) {
        for (var e : existingList) {
          if (Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        found.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoMovimentoId()));
        found.setTipo(dto.getTipo());
        found.setPercentagem(dto.getPercentagem());
        found.setValor(dto.getValor());
      } else {
        var entity = new ParamVinculoMovimentoEntity();
        entity.setVinculoId(vinculo);
        entity.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoMovimentoId()));
        entity.setTipo(dto.getTipo());
        entity.setPercentagem(dto.getPercentagem());
        entity.setValor(dto.getValor());
        entity.setEstado(Estado.A);
        entity.setUuid(UUID.randomUUID());
        existingList.add(entity);
      }
    }

    for (var existing : existingList) {
      if (existing.getUuid() == null) {
        existing.setUuid(UUID.randomUUID());
      }
      boolean stillExists = items.stream()
          .anyMatch(dto -> Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }

    repository.saveAll(existingList);

    return repository.findByVinculoId_IdAndEstadoNot(vinculo.getId(), Estado.E).stream()
        .map(this::toResponse)
        .toList();
  }

  private VinculoMovimentoResponseDTO toResponse(ParamVinculoMovimentoEntity entity) {
    var resp = new VinculoMovimentoResponseDTO();
    resp.setId(entity.getId());
    resp.setUuid(entity.getUuid());
    resp.setTipoMovimentoId(entity.getTmId() != null ? entity.getTmId().getId() : null);
    resp.setTipoMovimentoDescricao(entity.getTmId() != null ? entity.getTmId().getDescricao() : null);
    resp.setTipo(entity.getTipo());
    resp.setPercentagem(entity.getPercentagem());
    resp.setValor(entity.getValor());
    resp.setEstado(entity.getEstado() != null ? entity.getEstado().getCode() : null);
    return resp;
  }

}
