package cv.inps.rh.progressaopromocao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetListaSimulacaoProgressaPromocaoQuery implements Query {

  private String progressaoPromocao;

  private String colaborador;

  private String carreiraId;

  private String dataDe;

  private String dataAte;

  private String page;

  private String size;

  private String colaboradorId;
}
