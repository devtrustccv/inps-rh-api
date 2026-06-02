package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCalcValorHoraExtraQuery implements Query {

  @NotBlank(message = "The field funcionarioUuid is required")
  private String funcionarioUuid;

  @NotBlank(message = "The field dataInicio is required")
  private String dataInicio;

  @NotBlank(message = "The field dataFim is required")
  private String dataFim;

  @NotBlank(message = "The field percentagemReferente is required")
  private String percentagemReferente;

  @NotNull(message = "The field horasDiaria is required")
  private Long horasDiaria;

}
