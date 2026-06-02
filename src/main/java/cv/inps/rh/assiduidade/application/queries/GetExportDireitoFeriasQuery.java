package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetExportDireitoFeriasQuery implements Query {

  private Integer anoReferente;
  private Long direcaoId;

}
