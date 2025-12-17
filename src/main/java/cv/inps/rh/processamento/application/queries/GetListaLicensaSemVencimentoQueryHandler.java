package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.processamento.application.dto.WrapperListaColaboradorDTO;
import cv.inps.rh.processamento.domain.service.baixamedica.ColaboradorReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetListaLicensaSemVencimentoQueryHandler implements QueryHandler<GetListaLicensaSemVencimentoQuery, ResponseEntity<WrapperListaColaboradorDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetListaLicensaSemVencimentoQueryHandler.class);

  private final ColaboradorReadService colaboradorReadService;

  public GetListaLicensaSemVencimentoQueryHandler(ColaboradorReadService colaboradorReadService) {
    this.colaboradorReadService = colaboradorReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<WrapperListaColaboradorDTO> handle(GetListaLicensaSemVencimentoQuery query) {

    LOGGER.debug("GetListaLicensaSemVencimentoQuery: {}", query);

    var data = colaboradorReadService.getListaLicensaSemvencimento(query);

    return ResponseEntity.ok(data);
  }

}
