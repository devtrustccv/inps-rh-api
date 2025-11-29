package cv.inps.rh.parametrizacao.application.queries;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.DominioDTO;
import cv.inps.rh.parametrizacao.domain.models.Dominio;
import cv.inps.rh.parametrizacao.domain.repository.DomainsRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.DomainsMapper;

@Component
public class GetDominiosQueryHandler implements QueryHandler<GetDominiosQuery, ResponseEntity<List<DominioDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetDominiosQueryHandler.class);

  private final DomainsRepository domainsRepository;
  private final DomainsMapper domainsMapper;

  public GetDominiosQueryHandler(DomainsRepository domainsRepository, DomainsMapper domainsMapper) {

    this.domainsRepository = domainsRepository;
    this.domainsMapper = domainsMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<DominioDTO>> handle(GetDominiosQuery query) {
     String dominioParam = query.getDominio();
     String referenciaParam = query.getReferencia();

     List<Dominio> dominios = domainsRepository.findAllByDominio(dominioParam, referenciaParam);

     List<DominioDTO> dtos = dominios.stream()
         .map(domainsMapper::toDto)
         .toList();

     return ResponseEntity.ok(dtos);
  }

}
