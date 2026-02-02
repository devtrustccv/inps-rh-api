package cv.inps.rh.assiduidade.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaMovimentosResumidosQuery implements Query {

  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <colaborador> is required")
  private String colaborador;
  @NotNull(message = "The field <mes> is required")
  private Integer mes;
  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotBlank(message = "The field <estado> is required")
  private String estado;
  @NotNull(message = "The field <ilha> is required")
  private Long ilha;
  @NotNull(message = "The field <direcao> is required")
  private Long direcao;
  @NotNull(message = "The field <seccao> is required")
  private Long seccao;

}