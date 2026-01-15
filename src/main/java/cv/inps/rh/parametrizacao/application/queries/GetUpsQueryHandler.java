package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import cv.inps.rh.parametrizacao.application.dto.ParametrizacaoDTO;
import cv.inps.rh.shared.infrastructure.mappers.UpsMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.UpsEntityRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetUpsQueryHandler implements QueryHandler<GetUpsQuery, ResponseEntity<List<ParametrizacaoDTO>>> {

  private final UpsEntityRepository upsEntityRepository;
  private final UpsMapper upsMapper;

  public GetUpsQueryHandler(UpsEntityRepository upsEntityRepository, UpsMapper upsMapper) {
    this.upsEntityRepository = upsEntityRepository;
    this.upsMapper = upsMapper;
  }

  @IgrpQueryHandler
  public ResponseEntity<List<ParametrizacaoDTO>> handle(GetUpsQuery query) {

    var data = upsEntityRepository.findAll().stream().map(upsMapper::toParametrizacaoDto).toList();

    return ResponseEntity.ok(data);
  }
}
