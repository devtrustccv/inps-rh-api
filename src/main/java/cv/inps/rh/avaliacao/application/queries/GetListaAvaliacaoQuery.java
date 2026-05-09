package cv.inps.rh.avaliacao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaAvaliacaoQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotNull(message = "The field <direcao> is required")
  private Long direcao;
  @NotNull(message = "The field <cargo> is required")
  private Long cargo;
  @NotBlank(message = "The field <colaborador> is required")
  private String colaborador;

}
