package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaProcessosEtapaQuery implements Query {

  private String etapa;
  private String tipoProcesso;
  private String pageNumber;
  private String pageSize;

}
