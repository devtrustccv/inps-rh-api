package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.DefPagamento;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefPagamentoMapper {

  private final ContratoMapper contratoMapper;
  private final TiposRelacionamentoMapper tiprelMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;

  public DefPagamento toDomain(DefPagamentoEntity entity) {
    if (entity == null) return null;

    return DefPagamento.rebuild(
        entity.getId(),
        entity.getUuid(),
        contratoMapper.toDomain(entity.getContratoId()),
        tiprelMapper.toDomain(entity.getTiprelId()),
        entity.getValor(),
        tipoMovimentoMapper.toDomain(entity.getTmId()),
        entity.getDataInicio(),
        entity.getDataFim(),
        entity.getEstado(),
        entity.getObs()
    );
  }

  public DefPagamentoEntity toEntity(DefPagamento domain) {
    if (domain == null) return null;

    DefPagamentoEntity entity = new DefPagamentoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setValor(domain.getValor());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());

   /* if (domain.getContrato() != null) {
      entity.setContratoId(contratoMapper.toEntity(domain.getContrato()));
    }

    if (domain.getTiprel() != null) {
      entity.setTiprelId(tiprelMapper.toEntity(domain.getTiprel()));
    }*/

    if (domain.getTipoMovimento() != null) {
      entity.setTmId(entityManager.getReference(
          TipoMovimentoEntity.class,
          domain.getTipoMovimento().getId()
      ));
    }

    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());

    return entity;
  }
}
