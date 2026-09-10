package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaldoFeriaService {

  /** Valor de "nenhum pedido a excluir" — a query compara sempre, sem null. */
  private static final Long SEM_PEDIDO_A_EXCLUIR = -1L;

  private final FeriasEntityRepository feriasEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final AnoEntityRepository anoEntityRepository;
  private final FaltaEntityRepository faltaEntityRepository;


  /**
   * Saldo de férias já com a <b>reserva</b> das faltas pendentes: os dias que um pedido em
   * {@code P} tenciona deduzir em férias saem do saldo mal são pedidos, não só quando são
   * despachados.
   *
   * <p>Sem isto, dois pedidos pendentes viam ambos o saldo cheio e o segundo a ser despachado
   * descobria que já não havia nada — o desconto ia todo ao vencimento, sem ninguém ter sido
   * avisado no momento do registo.
   *
   * @param pedidoIdExcluir pedido a não reservar. No despacho é obrigatório passar o pedido
   *                        que está a ser aprovado: ele ainda está em {@code P} e, sem esta
   *                        exclusão, descontar-se-ia a si próprio.
   */
  public int getSaldo(UUID funcionarioId, Integer ano, Long pedidoIdExcluir) {

    final var funcionario = funcionarioEntityRepository
        .findByUuid(funcionarioId)
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

    final var excluir = pedidoIdExcluir != null ? pedidoIdExcluir : SEM_PEDIDO_A_EXCLUIR;

    int disponivel;
    long reservado;
    if (ano != null) {
      final var anoEntity = anoEntityRepository.findByAno(String.valueOf(ano))
          .orElseThrow(() -> new RuntimeException("Ano não encontrado"));

      // Calcula o saldo para o ano específico
      final var direitoAnual = feriasEntityRepository
          .findByFunId_UuidAndAnoId(funcionario.getUuid(), anoEntity)
          .map(FeriasEntity::getNumDia)
          .orElse(0);

      final var gozadoAnual = feriasGozadasEntityRepository.sumNumDiaByFuncionarioIdAndAno(funcionario.getUuid(),
          anoEntity.getId());

      disponivel = direitoAnual - gozadoAnual;
      reservado = faltaEntityRepository
          .countDiasPendentesDeducaoFeriasNoAno(funcionario.getUuid(), ano, excluir);
    } else {
      // Calcula o saldo total acumulado
      final var direitoTotal = feriasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());
      final var gozadoTotal = feriasGozadasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());

      disponivel = direitoTotal - gozadoTotal;
      reservado = faltaEntityRepository
          .countDiasPendentesDeducaoFerias(funcionario.getUuid(), excluir);
    }

    // A reserva desconta, mas nunca faz o saldo passar a negativo: é uma intenção, não um
    // consumo. Um saldo que já estava negativo antes da reserva (direito menor do que o
    // gozado, que existe em dados antigos) fica como estava — corrigi-lo aqui seria esconder
    // um problema de dados atrás de um cálculo novo.
    if (disponivel <= 0)
      return disponivel;

    return Math.max(0, disponivel - (int) reservado);

  }

  public int getSaldo(UUID funcionarioId, Integer ano) {
    return getSaldo(funcionarioId, ano, null);
  }

  public int getSaldo(UUID funcionarioId) {
    return getSaldo(funcionarioId, null, null);
  }
}
