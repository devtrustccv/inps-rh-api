package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDetalheMapaFeriaQuery implements Query {

  @NotBlank(message = "The field <mapaFeriaId> is required")
  private String mapaFeriaId;

}