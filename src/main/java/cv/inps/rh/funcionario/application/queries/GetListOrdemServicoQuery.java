package cv.inps.rh.funcionario.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListOrdemServicoQuery implements Query {

  @NotBlank(message = "The field <funcionarioUuid> is required")
  private String funcionarioUuid;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;

}
