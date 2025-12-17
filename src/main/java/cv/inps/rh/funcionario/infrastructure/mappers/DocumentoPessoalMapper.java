package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentoPessoalMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final EntityManager entityManager;



}
