package cv.inps.rh.emprestimo.domain.service.constants;

public enum TipoSituacao {

  ADIANTAMENTO_PAGAMENTO_ANTECIPADO,

  ADIANTAMENTO_DIMINUICAO_PRESTACAO,

  ADIANTAMENTO_PAGAMENTO_ANTECIPADO_DIMINUICAO_PRESTACAO,

  REFORCO_AUMENTO_VALOR,

  REFORCO_AUMENTO_PRESTACAO,

  REFORCO_AUMENTO_VALOR_AUMENTO_PRESTACAO,

  // Valores do domínio TIPO_SITUACAO_RENEGOCIACAO/RENEGOCIACAO (changelog v2
  // §12) — substituem os REFORCO_AUMENTO_* acima no fluxo de Renegociação de
  // Dívida (Reforço); mapeados na mesma lógica de plano financeiro em
  // EmprestimoHelper.saveByTipoSituacao.
  AUMENTO_PRESTACAO,

  REDUCAO_PRESTACAO,

  REFORCO_CAPITAL
}
