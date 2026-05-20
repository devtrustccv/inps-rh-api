package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetColaboresAumentoSalarialQuery implements Query {

  @NotNull(message = "The field <direcaoId> is required")
  private Long direcaoId;
  @NotNull(message = "The field <organicaId> is required")
  private Long organicaId;
  @NotNull(message = "The field <page> is required")
  private Integer page;
  @NotNull(message = "The field <size> is required")
  private Integer size;

}
