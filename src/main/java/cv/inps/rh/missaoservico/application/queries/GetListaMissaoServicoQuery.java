package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaMissaoServicoQuery implements Query {

  @NotBlank(message = "The field <nrMissao> is required")
  private String nrMissao;
  @NotBlank(message = "The field <periodoDe> is required")
  private String periodoDe;
  @NotBlank(message = "The field <periodoAte> is required")
  private String periodoAte;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;

}