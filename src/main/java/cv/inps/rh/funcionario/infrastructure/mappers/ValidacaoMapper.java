package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Validacao;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhValidacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidacaoMapper {

  private final TiposRelacionamentoMapper tiposRelacionamentoMapper;

  public RhValidacaoEntity toEntity(Validacao validacao) {
    if (validacao == null) return null;

    var entity = new RhValidacaoEntity();
    entity.setId(validacao.getId());
    entity.setObs(validacao.getObs());
    entity.setEstado(validacao.getEstado());
    entity.setUuid(validacao.getUuid().getValor());
    entity.setReferenciaName(validacao.getReferenciaName());
    //entity.setReferenciaId(); //sera setado depois na iteracao do agregado pai

    return  entity;
  }

  public Validacao toDomain(RhValidacaoEntity entity) {
    if (entity == null) return null;
    return Validacao.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoAccao(),
        entity.getReferenciaName(),
        entity.getReferenciaId(),
        entity.getEstado(),
        entity.getObs(),
        tiposRelacionamentoMapper.toDomain(entity.getTiprelId())
    );
  }


}
