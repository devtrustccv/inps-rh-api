package cv.inps.rh.funcionario.application.rules;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TipoSalarioVinculo;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Melhoria 25 — bloqueio dos movimentos fixos do vínculo quando o salário vem do PCCS.
 *
 * <p>Num vínculo SIM_PCCS, os movimentos do vínculo em RH_T_PARAM_VINCULO_MOV com TIPO = 'REM'
 * (vencimento) e TIPO = 'PAG' (IUR, INPS, valor líquido) ficam com FLG_BLOQUEAR = 1. Um registo
 * bloqueado, ou já processado (DATA_ULTIMO_PROC preenchida), só permite alterar Data Fim e OBS.</p>
 */
@Component
@RequiredArgsConstructor
public class MovimentoBloqueioRules {

  public static final String TIPO_REM = "REM";
  public static final String TIPO_PAG = "PAG";

  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final EntityManager entityManager;

  /** O movimento {@code tmId} é um fixo ({@code tipo} REM/PAG) de um vínculo PCCS? */
  public boolean deveBloquear(Long vinculoId, Long tmId, String tipo) {
    if (vinculoId == null || tmId == null) return false;
    var vinculo = entityManager.find(ParamVinculoEntity.class, vinculoId);
    if (vinculo == null || !TipoSalarioVinculo.ehPccs(vinculo.getFlgSalario())) return false;
    return paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(vinculoId, tipo, Estado.A)
        .stream()
        .anyMatch(m -> m.getTmId() != null && Objects.equals(m.getTmId().getId(), tmId));
  }

  /** Marca FLG_BLOQUEAR = 1 na remuneração se for o vencimento de um vínculo PCCS. */
  public void marcar(DefinicaoRemuneracaoEntity r, Long vinculoId) {
    if (r != null && r.getTmId() != null && deveBloquear(vinculoId, r.getTmId().getId(), TIPO_REM)) {
      r.setFlgBloquear(1);
    }
  }

  /** Marca FLG_BLOQUEAR = 1 no pagamento se for um IUR/INPS/valor líquido de um vínculo PCCS. */
  public void marcar(DefPagamentoEntity p, Long vinculoId) {
    if (p != null && p.getTmId() != null && deveBloquear(vinculoId, p.getTmId().getId(), TIPO_PAG)) {
      p.setFlgBloquear(1);
    }
  }

  /** Bloqueado pelo PCCS ou já processado: só Data Fim e OBS são editáveis. */
  public static boolean soDataFimEObs(DefinicaoRemuneracaoEntity r) {
    return r != null && (Objects.equals(r.getFlgBloquear(), 1) || r.getDataUltimoProc() != null);
  }

  /** Bloqueado pelo PCCS ou já processado: só Data Fim e OBS são editáveis. */
  public static boolean soDataFimEObs(DefPagamentoEntity p) {
    return p != null && (Objects.equals(p.getFlgBloquear(), 1) || p.getDataUltimoProc() != null);
  }
}
