package cv.inps.rh.progressaopromocao.application.queries;

import cv.igrp.framework.core.domain.Query;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetHistoricoProgressaPromocaoQuery implements Query {

  @NotBlank(message = "The field <progressaoPromocao> is required")
  private String progressaoPromocao;
  @NotBlank(message = "The field <colaborador> is required")
  private String colaborador;
  @NotBlank(message = "The field <carreiraId> is required")
  private String carreiraId;
  @NotBlank(message = "The field <dataDe> is required")
  private String dataDe;
  @NotBlank(message = "The field <dataAte> is required")
  private String dataAte;
  @NotBlank(message = "The field <page> is required")
  private String page;
  @NotBlank(message = "The field <size> is required")
  private String size;

}
