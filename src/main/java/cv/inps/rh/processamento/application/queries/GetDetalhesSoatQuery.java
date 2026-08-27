package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetDetalhesSoatQuery implements Query {

  @NotBlank(message = "SoatId is required")
  private String soatId;
  private Integer page;
  private Integer size;
}
