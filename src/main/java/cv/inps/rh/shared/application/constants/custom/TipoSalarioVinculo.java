package cv.inps.rh.shared.application.constants.custom;

/**
 * Domínio <b>TIPO_SALARIO_VINCULO</b> do campo "Tem Remuneração" do vínculo
 * (RH_T_PARAM_VINCULO.FLG_SALARIO). Substitui o antigo flag binário 0/1 por três valores:
 *
 * <ul>
 *   <li>{@link #SIM_PCCS} — Salário do PCCS (tem carreira/escalão; se o vínculo não tiver carreira,
 *       o escalão é gravado directamente em RH_T_TIPOS_RELACIONAMENTO.ESCALAO_ID);</li>
 *   <li>{@link #SIM_FORA_PCCS} — Salário fora do PCCS (introduzido manualmente, sem escalão);</li>
 *   <li>{@link #NAO} — o vínculo não tem remuneração.</li>
 * </ul>
 *
 * <p>Modelo "String crua + validação no service": FLG_SALARIO é uma String; os serviços validam o
 * valor contra este enum ao gravar ({@link #isValido(String)}) e interpretam-no ao ler pelos
 * helpers abaixo — que trabalham sobre a String para tolerar dados legados (o antigo 0/1 ainda pode
 * existir em BD enquanto o backfill não corre).</p>
 *
 * <p><b>Regra de segurança:</b> valores legados ou desconhecidos <b>nunca</b> contam como PCCS
 * ({@link #ehPccs(String)} só é verdadeiro para o literal {@code SIM_PCCS}). Assim, mesmo antes do
 * backfill, a lógica de escalão-no-tiprel não dispara indevidamente para dados antigos.</p>
 */
public enum TipoSalarioVinculo {
  SIM_PCCS,
  SIM_FORA_PCCS,
  NAO;

  /** {@code true} se {@code valor} é exactamente um dos três valores do domínio (para validar no save). */
  public static boolean isValido(String valor) {
    if (valor == null) return false;
    var v = valor.trim().toUpperCase();
    for (var t : values()) if (t.name().equals(v)) return true;
    return false;
  }

  /**
   * {@code true} se o vínculo tem remuneração (equivalente ao antigo {@code flg_salario == 1}).
   * Tolera o legado numérico: "0" → sem salário, "1"/qualquer valor de salário → tem salário.
   * Só {@link #NAO} (ou "0"/null/vazio) significa "não tem salário".
   */
  public static boolean temSalario(String valor) {
    if (valor == null) return false;
    var v = valor.trim().toUpperCase();
    if (v.isEmpty()) return false;
    return !NAO.name().equals(v) && !"0".equals(v);
  }

  /**
   * {@code true} apenas para {@link #SIM_PCCS}. Usado para decidir se o escalão vai para a carreira
   * (quando o vínculo tem carreira) ou directamente para o tiprel (quando não tem). Valores legados
   * ("1") ou desconhecidos devolvem {@code false} — por design, para não tratar dados por classificar
   * como PCCS.
   */
  public static boolean ehPccs(String valor) {
    return valor != null && SIM_PCCS.name().equals(valor.trim().toUpperCase());
  }
}
