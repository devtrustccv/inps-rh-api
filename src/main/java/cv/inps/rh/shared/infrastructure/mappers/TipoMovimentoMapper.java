package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.Instituicao;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TipoMovimentoMapper {

  public TipoMovimento toDomain(TipoMovimentoEntity tipoMovimentoEntity){
    if (tipoMovimentoEntity == null) return null;
    return TipoMovimento
        .rebuild(tipoMovimentoEntity.getId(), tipoMovimentoEntity.getDescricao(), tipoMovimentoEntity.getValor(), tipoMovimentoEntity.getPercentagem(),
            tipoMovimentoEntity.getTipo());
  }

  public TipoMovimento toDomain(Long idTipoMovimento) {
    if (idTipoMovimento == null) return null;
    return TipoMovimento
        .rebuild(idTipoMovimento, null, null, null, null);
  }

  public ParametrizacaoDTO toParametrizacaoDto(TipoMovimento tipoMovimento) {
    if (tipoMovimento == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(tipoMovimento.getDescricao());
    dto.setValue(tipoMovimento.getId());
    return dto;
  }
}
