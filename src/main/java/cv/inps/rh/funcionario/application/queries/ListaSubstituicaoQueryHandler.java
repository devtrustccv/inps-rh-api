package cv.inps.rh.funcionario.application.queries;

import cv.inps.rh.funcionario.application.service.SubstituicaoReadService;
import cv.inps.rh.funcionario.application.service.SubstituicaoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.funcionario.application.dto.SubstituicaoSumaryDTO;

@Component
public class ListaSubstituicaoQueryHandler implements QueryHandler<ListaSubstituicaoQuery, ResponseEntity<List<SubstituicaoSumaryDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(ListaSubstituicaoQueryHandler.class);

   private final SubstituicaoReadService substituicaoReadService;

  public ListaSubstituicaoQueryHandler(SubstituicaoReadService substituicaoReadService) {

    this.substituicaoReadService = substituicaoReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<SubstituicaoSumaryDTO>> handle(ListaSubstituicaoQuery query) {
    return ResponseEntity.ok(substituicaoReadService.listar(query));
  }

}
