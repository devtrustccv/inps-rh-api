package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.DefinicaoRemuneracao;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefinicaoRemuneracaoMapper {

  private final ContratoMapper contratoMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;

  // Entity -> Domain
  public DefinicaoRemuneracao toDomain(DefinicaoRemuneracaoEntity entity) {
    if (entity == null) return null;

    return DefinicaoRemuneracao.rebuild(
        entity.getId(),
        entity.getUuid(),
        contratoMapper.toDomain(entity.getContratoId()),
        entity.getPercentagem(),
        entity.getValor(),
        entity.getEstado(),
        entity.getObs(),
        tipoMovimentoMapper.toDomain(entity.getTmId()),
        entity.getDataInicio(),
        entity.getDataFim()
    );
  }

  // Domain -> Entity
  public DefinicaoRemuneracaoEntity toEntity(DefinicaoRemuneracao domain) {
    if (domain == null) return null;

    DefinicaoRemuneracaoEntity entity = new DefinicaoRemuneracaoEntity();
    entity.setId(domain.getId());
    entity.setUuid(domain.getUuid().getValor());
    entity.setPercentagem(domain.getPercentagem());
    entity.setValor(domain.getValor());
    entity.setEstado(domain.getEstado());
    entity.setObs(domain.getObs());
    entity.setDataInicio(domain.getDataInicio());
    entity.setDataFim(domain.getDataFim());

    if (domain.getTipoMovimento() != null) {
      entity.setTmId(entityManager.getReference(
          TipoMovimentoEntity.class,
          domain.getTipoMovimento().getId()
      ));
    }
    return entity;
  }
}
