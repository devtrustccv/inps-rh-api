package cv.inps.rh.funcionario.application.constants.custom;

import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;

public enum MotivoSituacaoLaboral {
  // ATIVO
  RETORNO_DE_LICENCA(SituacaoLaboral.ATIVO),
  NOMEACAO_DESIGNACAO(SituacaoLaboral.ATIVO),
  ESTAGIO_CURRICULAR_PROFISSIONAL(SituacaoLaboral.ATIVO),
  MOBILIDADE(SituacaoLaboral.ATIVO),
  PROGRESSAO_PROMOCAO(SituacaoLaboral.ATIVO),
  ADMISSAO(SituacaoLaboral.ATIVO),

  // SUSPENSO
  LICENCA_SEM_VENCIMENTO(SituacaoLaboral.SUSPENSO),
  DOENCA(SituacaoLaboral.SUSPENSO),
  MATERNIDADE_PATERNIDADE( SituacaoLaboral.SUSPENSO),
  ACIDENTE_DE_TRABALHO(SituacaoLaboral.SUSPENSO),
  FORMACAO(SituacaoLaboral.SUSPENSO),
  OUTROS_ATIVO(SituacaoLaboral.SUSPENSO),

  // CESSADO
  APOSENTACAO(SituacaoLaboral.CESSADO),
  DEMISSAO_VOLUNTARIA(SituacaoLaboral.CESSADO),
  DESPEDIMENTO(SituacaoLaboral.CESSADO),
  ACORDO_DE_REVOGACAO(SituacaoLaboral.CESSADO),
  INVALIDEZ(SituacaoLaboral.CESSADO),
  FALECIMENTO(SituacaoLaboral.CESSADO),
  FIM_DA_COMISSAO(SituacaoLaboral.CESSADO),
  EXONERACAO(SituacaoLaboral.CESSADO),
  FIM_DO_DESTAQUE(SituacaoLaboral.CESSADO),
  NAO_RENOVACAO(SituacaoLaboral.CESSADO),
  OUTROS_CESSADO(SituacaoLaboral.CESSADO),


  // DISPONÍVEL/RESERVA
  REQUALIFICACAO(SituacaoLaboral.DISPONIVEL_RESERVA),
  QUADRO_DE_EXCEDENTES(SituacaoLaboral.DISPONIVEL_RESERVA);

  private final SituacaoLaboral categoria;

  MotivoSituacaoLaboral(SituacaoLaboral categoria) {
    this.categoria = categoria;
  }

  public SituacaoLaboral getCategoria() {
    return categoria;
  }
}
