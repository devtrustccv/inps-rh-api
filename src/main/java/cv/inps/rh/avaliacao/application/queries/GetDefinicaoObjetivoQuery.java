package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDefinicaoObjetivoQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}