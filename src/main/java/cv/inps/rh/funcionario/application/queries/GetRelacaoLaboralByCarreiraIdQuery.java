package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetRelacaoLaboralByCarreiraIdQuery implements Query {

  @NotBlank(message = "The field <carreiraId> is required")
  private String carreiraId;

}