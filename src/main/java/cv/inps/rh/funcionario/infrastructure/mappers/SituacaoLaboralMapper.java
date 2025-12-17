package cv.inps.rh.funcionario.infrastructure.mappers;


import cv.inps.rh.parametrizacao.infrastructure.mappers.ParamSitLaboralMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SituacaoLaboralMapper {

  private final ParamSitLaboralMapper paramSitLaboralMapper;
  private final EntityManager entityManager;

}
