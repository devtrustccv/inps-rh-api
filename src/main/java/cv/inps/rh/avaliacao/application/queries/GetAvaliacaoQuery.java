package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAvaliacaoQuery implements Query {

  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
