package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetObjectivosComunsQuery implements Query {

  @NotNull(message = "The field <ano> is required")
  private Integer ano;

  /** Período a mostrar. Em branco devolve as linhas sem medições. */
  private String periodicidade;

}
