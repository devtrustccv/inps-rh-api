package cv.inps.rh.missaoservico.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAvaliacaoPrestadorQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;
  @NotBlank(message = "The field <tipoProcesso> is required")
  private String tipoProcesso;
  @NotBlank(message = "The field <missaoPrestUuid> is required")
  private String missaoPrestUuid;

}
