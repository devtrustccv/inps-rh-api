package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.RenovacaoDetalheDTO;
import cv.inps.rh.funcionario.application.service.RenovacaoContratoReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GetRenovacaoDetalheQueryHandler implements QueryHandler<GetRenovacaoDetalheQuery, ResponseEntity<RenovacaoDetalheDTO>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetRenovacaoDetalheQueryHandler.class);

  private final RenovacaoContratoReadService renovacaoContratoReadService;

  public GetRenovacaoDetalheQueryHandler(RenovacaoContratoReadService renovacaoContratoReadService) {
    this.renovacaoContratoReadService = renovacaoContratoReadService;
  }

  @IgrpQueryHandler
  public ResponseEntity<RenovacaoDetalheDTO> handle(GetRenovacaoDetalheQuery query) {

    var data = renovacaoContratoReadService.getDetalhe(query.getContratoId());

    return ResponseEntity.ok(data);
  }

}
