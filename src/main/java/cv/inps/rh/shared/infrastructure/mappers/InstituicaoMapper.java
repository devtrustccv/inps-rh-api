package cv.inps.rh.shared.infrastructure.mappers;

import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.domain.models.Instituicao;
import cv.inps.rh.shared.infrastructure.persistence.entity.InstituicaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstituicaoMapper {

  public Instituicao toDomain(InstituicaoEntity instituicaoEntity){
    if (instituicaoEntity == null) return null;
    return Instituicao
        .rebuild(instituicaoEntity.getId(), instituicaoEntity.getNome(), instituicaoEntity.getCodigo());
  }

  public Instituicao toDomain(Long idInstituicao) {
    if (idInstituicao == null) return null;
    return Instituicao
        .rebuild(idInstituicao, null, null);
  }

  public ParametrizacaoDTO toParametrizacaoDto(InstituicaoEntity instituicao) {
    if (instituicao == null) return null;

    ParametrizacaoDTO dto = new ParametrizacaoDTO();
    dto.setLabel(instituicao.getNome());
    dto.setValue(instituicao.getId());
    return dto;
  }

}
