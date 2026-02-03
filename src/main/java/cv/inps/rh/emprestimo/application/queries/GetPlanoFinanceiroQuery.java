package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetPlanoFinanceiroQuery implements Query {

  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}
