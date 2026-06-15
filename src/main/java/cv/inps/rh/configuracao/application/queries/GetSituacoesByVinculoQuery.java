package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSituacoesByVinculoQuery implements Query {

  @NotBlank(message = "The field <vinculoUuid> is required")
  private String vinculoUuid;

}
