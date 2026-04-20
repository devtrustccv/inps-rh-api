package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetResponsaveisQuery implements Query {

  @NotBlank(message = "The field <pageNumber> is required")
  private String pageNumber;
  @NotBlank(message = "The field <pageSize> is required")
  private String pageSize;
  @NotBlank(message = "The field <nomeFuncionario> is required")
  private String nomeFuncionario;
  @NotBlank(message = "The field <nomeInstituicao> is required")
  private String nomeInstituicao;
  @NotNull(message = "The field <idInstituicao> is required")
  private Long idInstituicao;
  @NotBlank(message = "The field <nomeSecccao> is required")
  private String nomeSecccao;
  @NotNull(message = "The field <idSeccao> is required")
  private Long idSeccao;

}
