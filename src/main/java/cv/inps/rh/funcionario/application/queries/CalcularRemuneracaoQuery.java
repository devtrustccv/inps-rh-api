package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularRemuneracaoQuery implements Query {

  @Valid
  @NotNull(message = "The field <request> is required")
  private CalcularRemuneracaoRequestDTO request;

}
