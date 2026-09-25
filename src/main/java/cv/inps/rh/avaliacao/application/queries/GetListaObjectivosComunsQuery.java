package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaObjectivosComunsQuery implements Query {

  /** Filtro Ano (RH_T_AVD.ANO). Opcional. */
  private Integer ano;
  private String pageNumber;
  private String pageSize;

}
