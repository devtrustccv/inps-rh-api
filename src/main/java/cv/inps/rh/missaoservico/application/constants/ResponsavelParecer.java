package cv.inps.rh.missaoservico.application.constants;

/** Quem emite o parecer — RH_T_MISSAO_PROCESSO_DET.RESPONSAVEL. */
public enum ResponsavelParecer {

  /** Etapa Validação UGAL. */
  UGAL("UGAL"),
  /** Etapa Aprovação RH — primeiro parecer, obrigatório mas não vinculativo (D3). */
  COORDENADOR_RH("Coordenador RH"),
  /** Etapa Aprovação RH — parecer que decide. */
  DIRECTOR_RH("Director RH");

  private final String descricao;

  ResponsavelParecer(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
