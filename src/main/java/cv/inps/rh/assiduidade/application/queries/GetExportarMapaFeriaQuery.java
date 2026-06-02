package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetExportarMapaFeriaQuery implements Query {

  @NotNull(message = "The field ano is required")
  private Integer ano;

  @NotNull(message = "The field direcao is required")
  private Long direcao;

}
