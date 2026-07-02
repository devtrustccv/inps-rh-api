package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaAumentoSalarialQuery implements Query {

  private Integer ano;

  private Integer page;

  private Integer size;
}
