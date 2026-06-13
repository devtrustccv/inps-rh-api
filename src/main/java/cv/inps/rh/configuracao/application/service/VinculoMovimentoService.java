package cv.inps.rh.configuracao.application.service;

import cv.inps.rh.configuracao.application.dto.VinculoMovimentoRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoMovimentoResponseDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VinculoMovimentoService {

  private final ParamVinculoMovimentoEntityRepository repository;
  private final EntityManager entityManager;

  @Transactional(readOnly = true)
  public List<VinculoMovimentoResponseDTO> listarPorVinculo(Long vinculoId) {
    return repository.findByVinculoId_Id(vinculoId).stream()
        .filter(m -> m.getEstado() != Estado.E)
        .map(this::toResponse)
        .toList();
  }

  @Transactional
  public VinculoMovimentoResponseDTO criar(Long vinculoId, VinculoMovimentoRequestDTO dto) {
    var vinculo = ValidationUtil.ref(entityManager, ParamVinculoEntity.class, vinculoId);
    var tipoMovimento = ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoMovimentoId());

    var entity = new ParamVinculoMovimentoEntity();
    entity.setVinculoId(vinculo);
    entity.setTmId(tipoMovimento);
    entity.setTipo(dto.getTipo());
    entity.setPercentagem(dto.getPercentagem());
    entity.setValor(dto.getValor());
    entity.setEstado(Estado.A);
    entity.setUuid(UUID.randomUUID());

    return toResponse(repository.save(entity));
  }

  @Transactional
  public VinculoMovimentoResponseDTO editar(Long vinculoId, Long id, VinculoMovimentoRequestDTO dto) {
    var entity = repository.findByIdOrThrow(id);

    if (!entity.getVinculoId().getId().equals(vinculoId)) {
      throw IgrpResponseStatusException.badRequest("O movimento não pertence ao vínculo informado");
    }

    var tipoMovimento = ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoMovimentoId());

    entity.setTmId(tipoMovimento);
    entity.setTipo(dto.getTipo());
    entity.setPercentagem(dto.getPercentagem());
    entity.setValor(dto.getValor());

    return toResponse(repository.save(entity));
  }

  @Transactional
  public void eliminar(Long vinculoId, Long id) {
    var entity = repository.findByIdOrThrow(id);

    if (!entity.getVinculoId().getId().equals(vinculoId)) {
      throw IgrpResponseStatusException.badRequest("O movimento não pertence ao vínculo informado");
    }

    entity.setEstado(Estado.I);
    repository.save(entity);
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
