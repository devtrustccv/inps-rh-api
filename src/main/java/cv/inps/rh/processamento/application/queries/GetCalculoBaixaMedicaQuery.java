package cv.inps.rh.processamento.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCalculoBaixaMedicaQuery implements Query {

  private UUID colaborador;
  private String dataInicio;
  private String dataFim;
  private Long tipoLicenca;
  private String dataInicioFalta;

}
