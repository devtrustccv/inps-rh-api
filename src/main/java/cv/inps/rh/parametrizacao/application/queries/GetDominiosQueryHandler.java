package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.parametrizacao.domain.models.Dominio;
import cv.inps.rh.parametrizacao.domain.repository.DomainsRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.DomainsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import cv.inps.rh.parametrizacao.application.dto.DominioDTO;
import org.springframework.stereotype.Repository;

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

     List<Dominio> dominios = domainsRepository.findAllByDominio(dominioParam);

     List<DominioDTO> dtos = dominios.stream()
         .map(domainsMapper::toDto)
         .toList();

     return ResponseEntity.ok(dtos);
  }

}
