package cv.inps.rh.parametrizacao.application.queries;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.domain.repository.SecaoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.SecaoMapper;
import org.springframework.transaction.annotation.Transactional;

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
  @Transactional
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetSeccoesAtivosQuery query) {
    var secoes =  secaoRepository.findAllActiveByInstitId(query.getInstitId());
    List<ParametrizacaoDTO> parametrizacoes = secoes.stream()
        .map(secaoMapper::toParametrizacaoDto)
        .toList();
    return ResponseEntity.ok(parametrizacoes);

  }

}
