package cv.inps.rh.emprestimo.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetHistoricoPagamentoQuery implements Query {

  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}