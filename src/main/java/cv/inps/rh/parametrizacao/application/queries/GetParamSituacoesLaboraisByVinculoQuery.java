package cv.inps.rh.parametrizacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetParamSituacoesLaboraisByVinculoQuery implements Query {

  @NotNull(message = "The field <vinculoId> is required")
  private Long vinculoId;
  @NotBlank(message = "The field <flgEstadoContrato> is required")
  private String flgEstadoContrato;

}
