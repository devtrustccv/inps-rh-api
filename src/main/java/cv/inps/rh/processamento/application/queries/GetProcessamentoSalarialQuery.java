package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetProcessamentoSalarialQuery implements Query {

  private String dataInicio;

  private String dataFim;

  private String direcaoId;

  private String tipo;

  private String estado;

  private String page;

  private String size;
}
