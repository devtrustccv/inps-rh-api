package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetColaboresAumentoSalarialQuery implements Query {

  private Long direcaoId;

  private Long organicaId;

  private Integer page;

  private Integer size;
}
