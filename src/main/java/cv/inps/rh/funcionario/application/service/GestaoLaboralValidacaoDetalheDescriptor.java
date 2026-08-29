package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.service.historicolaboral.EscalaoDetalheDiffWriter;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.ValidacaoDetalheDescriptor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Descritor da grelha "Detalhe de alterações" para o movimento "Alterar Escalão/Cargo" (referência
 * {@code ALTERACAO_ESCALAO}, melhoria 2.2.1). O movimento altera o próprio tiprel
 * (RH_T_TIPOS_RELACIONAMENTO) — para vínculos sem carreira o escalão/cargo/salário vivem lá.
 *
 * <p><b>Nota:</b> ao contrário dos outros descritores, esta referência NÃO é lida pelo
 * {@code JaversValidacaoDetalheReadService} — o tiprel é Shallow Reference e o JaVers grava-o vazio. A
 * grelha é servida pelo {@code AlteracaoEscalaoDetalheReadService}, que reutiliza os {@link #rotulos()} e
 * a allow-list {@link #camposNegocio()} deste descritor como fonte única de rótulos/campos.
 */
@Component
public class GestaoLaboralValidacaoDetalheDescriptor implements ValidacaoDetalheDescriptor {

  @Override
  public String referenciaName() {
    return Referencia.ALTERACAO_ESCALAO.name();
  }

  @Override
  public String entityTypeSuffix() {
    return "TiposRelacionamentoEntity";
  }

  // Campos e rótulos derivam do Snapshot do EscalaoDetalheDiffWriter (fonte única: as anotações @Rotulo).
  // Fora da grelha, por omissão: estado (workflow), fun/contr/carreira/mob/regime/situac (estruturais),
  // est_act_adm/flg_processa (workflow), created*/lastModified*.

  @Override
  public Set<String> camposNegocio() {
    return EscalaoDetalheDiffWriter.rotulos().keySet();
  }

  @Override
  public Map<String, String> rotulos() {
    return EscalaoDetalheDiffWriter.rotulos();
  }
}
