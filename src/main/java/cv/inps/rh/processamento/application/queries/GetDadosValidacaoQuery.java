package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDadosValidacaoQuery implements Query {

  private String tipoValidacao;

  private String mesAtual;

  private String mesAnterior;

  private String processamentoIds;
}
