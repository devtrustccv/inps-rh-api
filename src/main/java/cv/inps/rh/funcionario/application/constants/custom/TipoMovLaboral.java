package cv.inps.rh.funcionario.application.constants.custom;

import cv.inps.rh.shared.application.constants.custom.Referencia;
import lombok.Getter;

@Getter
public enum TipoMovLaboral {

  // Grupo CONTRATO
  INICIO_CONTRATO(Referencia.CONTRATO),
  RENOVACAO_CONTRATO(Referencia.RENOVACAO_CONTRATO),
  CONTINUIDADE_CONTRATO(Referencia.CONTRATO),

  // Grupo MOBILIDADE
  CARGO(Referencia.MOBILIDADE),
  SECCAO(Referencia.MOBILIDADE),
  LOCAL_TRABALHO(Referencia.MOBILIDADE),
  DIRECAO(Referencia.MOBILIDADE),

  // Grupo CARREIRA
  PROGRESSAO(Referencia.CARREIRA),
  PROMOCAO(Referencia.CARREIRA),
  CONTINUIDADE_CARREIRA(Referencia.CARREIRA),
  MUDANCA_CARREIRA(Referencia.CARREIRA),
  REPOSICIONAMENTO_PCCS(Referencia.CARREIRA),

  // Grupo REGIME
  MUDANCA_REGIME(Referencia.REGIME),

  // Grupo SITUACAO_LABORAL
  MUDANCA_SITUACAO_LABORAL(Referencia.SITUACAO_LABORAL);

  private final Referencia referencia;

  TipoMovLaboral(Referencia referencia) {
    this.referencia = referencia;
  }

}
