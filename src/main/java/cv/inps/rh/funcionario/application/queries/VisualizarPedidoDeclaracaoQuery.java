package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisualizarPedidoDeclaracaoQuery implements Query {

  @NotNull(message = "The field <preview> is required")
  private boolean preview;
  @NotBlank(message = "The field <id> is required")
  private String id;

}