package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDadosApolicesAtivosQuery implements Query {

  private Integer page;
  private Integer size;
}
