package cv.inps.rh.shared.domain.service.ordemservico;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVRelacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVRelacaoLaboralEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Dados comuns às ordens de serviço.
 *
 * <p>A ordem de serviço é escolhida na validação, quando o colaborador costuma ter um vínculo
 * PENDENTE (novo contrato, alteração de escalão, mobilidade…) além do actual. A relação laboral
 * usada é a desse vínculo pendente; sem pendente, a actual (EST_ACT_ADM = 1).</p>
 */
@Component
@RequiredArgsConstructor
public class OrdemServicoDados {

  /** Valor mostrado quando o dado não existe (ex.: colaborador sem cargo ou sem secção). */
  public static final String SEM_VALOR = "—";

  private final RhVRelacaoLaboralEntityRepository relacaoLaboralRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;

  public RhVRelacaoLaboralEntity relacaoLaboral(String funcionarioUuid) {
    var pendente = tiposRelacionamentoRepository
        .findFirstByFunId_UuidAndEstadoOrderByIdDesc(UUID.fromString(funcionarioUuid), Estado.P);
    if (pendente.isPresent()) {
      var linha = relacaoLaboralRepository.findById(pendente.get().getId());
      if (linha.isPresent()) return linha.get();
    }
    return relacaoLaboralRepository.findFirstByFuncionarioUuidAndEstActAdm(funcionarioUuid, 1L)
        .or(() -> relacaoLaboralRepository.findFirstByFuncionarioUuidOrderByEstActAdmDesc(funcionarioUuid))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Relação laboral não encontrada para o colaborador: " + funcionarioUuid));
  }

  /**
   * Variáveis do template (pares nome, valor). Ao contrário de {@code Map.of}, aceita valores nulos
   * e substitui-os por "—" — um dado em falta não pode impedir a ordem de serviço (era um 500).
   */
  public static Map<String, Object> vars(Object... nomeValor) {
    var out = new LinkedHashMap<String, Object>();
    for (int i = 0; i + 1 < nomeValor.length; i += 2) {
      var valor = nomeValor[i + 1];
      out.put(String.valueOf(nomeValor[i]), valor == null || String.valueOf(valor).isBlank() ? SEM_VALOR : valor);
    }
    return out;
  }
}
