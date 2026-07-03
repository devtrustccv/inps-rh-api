package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.application.dto.TipoMovimentoDTO;
import cv.inps.rh.shared.domain.models.TipoMovimento;
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

  public TipoMovimentoDTO toParametrizacaoDto(TipoMovimentoEntity tipoMovimento) {
    if (tipoMovimento == null) return null;

    TipoMovimentoDTO dto = new TipoMovimentoDTO();
    dto.setLabel(tipoMovimento.getDescricao());
    dto.setValue(tipoMovimento.getId());
    dto.setValor(tipoMovimento.getValor());
    dto.setPercentagem(tipoMovimento.getPercentagem());

    return dto;
  }
}
