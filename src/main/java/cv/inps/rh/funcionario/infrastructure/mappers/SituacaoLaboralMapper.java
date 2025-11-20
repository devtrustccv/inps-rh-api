package cv.inps.rh.funcionario.infrastructure.mappers;


import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSitLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SituacaoLaboralMapper {

  private final ParamSitLaboralMapper paramSitLaboralMapper;
  private final EntityManager entityManager;

}
