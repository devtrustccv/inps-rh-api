package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.parametrizacao.application.dto.TipoDocumentoDTO;
import cv.inps.rh.parametrizacao.domain.repository.TipoDocumentoRepository;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetTiposDocumentoAtivosQueryHandler implements QueryHandler<GetTiposDocumentoAtivosQuery, ResponseEntity<List<TipoDocumentoDTO>>>{

  private static final Logger LOGGER = LoggerFactory.getLogger(GetTiposDocumentoAtivosQueryHandler.class);

  private final TipoDocumentoRepository tipoDocumentoRepository;
  private final TipoDocumentoMapper tipoDocumentoMapper;

  public GetTiposDocumentoAtivosQueryHandler(TipoDocumentoRepository tipoDocumentoRepository, TipoDocumentoMapper tipoDocumentoMapper) {

    this.tipoDocumentoRepository = tipoDocumentoRepository;
    this.tipoDocumentoMapper = tipoDocumentoMapper;
  }

   @IgrpQueryHandler
  public ResponseEntity<List<TipoDocumentoDTO>> handle(GetTiposDocumentoAtivosQuery query) {
     var tiposDocumentos =  tipoDocumentoRepository.findAllActive(query.getReferencia());
     List<TipoDocumentoDTO> parametrizacoes = tiposDocumentos.stream()
         .map(tipoDocumentoMapper::toParametrizacaoDto)
         .toList();
     return ResponseEntity.ok(parametrizacoes);
  }

}
