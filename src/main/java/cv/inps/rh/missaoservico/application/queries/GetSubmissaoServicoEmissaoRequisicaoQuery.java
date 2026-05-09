package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSubmissaoServicoEmissaoRequisicaoQuery implements Query {

  @NotBlank(message = "The field <uui> is required")
  private String uui;

}
