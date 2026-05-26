package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamSituacoesLaboraisByVinculoQuery implements Query {

  @NotNull(message = "The field <vinculoId> is required")
  private Long vinculoId;
  @NotBlank(message = "The field <flgEstadoContrato> is required")
  private String flgEstadoContrato;
  @NotBlank(message = "The field <flgAbonoBeneficio> is required")
  private String flgAbonoBeneficio;
  @NotBlank(message = "The field <codigo> is required")
  private String codigo;

}