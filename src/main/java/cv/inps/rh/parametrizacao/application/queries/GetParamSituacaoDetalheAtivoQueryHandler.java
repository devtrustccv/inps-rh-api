package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSituacaoDetalheEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;

@Component
public class GetParamSituacaoDetalheAtivoQueryHandler implements QueryHandler<GetParamSituacaoDetalheAtivoQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetParamSituacaoDetalheAtivoQueryHandler.class);

  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;
  public GetParamSituacaoDetalheAtivoQueryHandler(ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository) {

    this.paramSituacaoDetalheEntityRepository = paramSituacaoDetalheEntityRepository;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetParamSituacaoDetalheAtivoQuery query) {
      LOGGER.info("GetParamSituacaoDetalheAtivoQueryHandler.handle: {}", query);

      Long situacaoLaboralId = query.getSituacaoLaboralId();
     var lista = paramSituacaoDetalheEntityRepository.findAllBySituacaoLaboralId_IdAndEstado(situacaoLaboralId, Estado.A);

     var response = lista.stream()
         .map(e -> {
           ParametrizacaoDTO dto = new ParametrizacaoDTO();
           dto.setValue(e.getId());
           dto.setLabel(e.getMotivo());
           return dto;
         })
         .toList();

     return ResponseEntity.ok(response);
  }

}
