package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.application.service.FamiliaresReadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetDadosFamiliaresQueryHandler implements QueryHandler<GetDadosFamiliaresQuery, ResponseEntity<List<AgregadoDependenteRespDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDadosFamiliaresQueryHandler.class);

  private final FamiliaresReadService familiaresReadService;

  public GetDadosFamiliaresQueryHandler(FamiliaresReadService familiaresReadService) {

    this.familiaresReadService = familiaresReadService;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<AgregadoDependenteRespDTO>> handle(GetDadosFamiliaresQuery query) {

    return ResponseEntity.ok(familiaresReadService.getFamiliares(query));
  }

}
