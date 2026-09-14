package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaPrestadorServicoQuery implements Query {

  private String nome;
  private String ilhaId;
  private String estado;
  private String pageNumber;
  private String pageSize;

}
