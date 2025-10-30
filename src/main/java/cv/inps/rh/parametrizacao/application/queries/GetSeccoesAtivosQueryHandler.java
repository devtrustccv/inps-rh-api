package cv.inps.rh.parametrizacao.application.queries;

import cv.inps.rh.parametrizacao.domain.repository.SecaoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.SecaoMapper;
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
public class GetSeccoesAtivosQueryHandler implements QueryHandler<GetSeccoesAtivosQuery, ResponseEntity<List<ParametrizacaoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetSeccoesAtivosQueryHandler.class);

  private final SecaoRepository secaoRepository;
  private final SecaoMapper secaoMapper;

  public GetSeccoesAtivosQueryHandler(SecaoRepository secaoRepository, SecaoMapper secaoMapper) {

    this.secaoRepository = secaoRepository;
    this.secaoMapper = secaoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetSeccoesAtivosQuery query) {
    var secoes =  secaoRepository.findAllActive();
    List<ParametrizacaoDTO> parametrizacoes = secoes.stream()
        .map(secaoMapper::toParametrizacaoDto)
        .toList();
    return ResponseEntity.ok(parametrizacoes);

  }

}
