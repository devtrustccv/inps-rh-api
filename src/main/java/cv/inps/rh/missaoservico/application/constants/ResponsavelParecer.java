package cv.inps.rh.missaoservico.application.constants;

/** Quem emite o parecer — RH_T_MISSAO_PROCESSO_DET.RESPONSAVEL. */
public enum ResponsavelParecer {

  /** Etapa Validação UGAL. */
  UGAL,
  /** Etapa Aprovação RH — primeiro parecer, obrigatório mas não vinculativo (D3). */
  COORDENADOR_RH,
  /** Etapa Aprovação RH — parecer que decide. */
  DIRECTOR_RH
}
