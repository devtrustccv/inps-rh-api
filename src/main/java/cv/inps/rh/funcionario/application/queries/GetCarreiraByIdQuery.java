package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCarreiraByIdQuery implements Query {

  @NotBlank(message = "The field <carreiraId> is required")
  private String carreiraId;

}
