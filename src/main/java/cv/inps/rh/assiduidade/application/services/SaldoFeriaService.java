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
    return detalhe(funcionarioId, ano, pedidoIdExcluir).saldo();
  }

  /**
   * O mesmo saldo, mas com as parcelas que lhe deram origem. O endpoint devolvia so o liquido,
   * e um {@code 0} nao dizia se o colaborador nao tem direito ou se ja gastou tudo.
   *
   * <p>{@code anoReferencia} nulo significa <b>acumulado de todos os anos</b>, que e o que o
   * calculo faz quando nao se pede um ano — dai o campo {@code ambito}. Nao se assume aqui o ano
   * activo: seis chamadores decidem dinheiro com este numero e mudar o omisso mudava-lhes a conta.
   */
  public SaldoFeriasDetalhe detalhe(UUID funcionarioId, Integer ano, Long pedidoIdExcluir) {

    final var funcionario = funcionarioEntityRepository
        .findByUuid(funcionarioId)
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

    final var excluir = pedidoIdExcluir != null ? pedidoIdExcluir : SEM_PEDIDO_A_EXCLUIR;

    int disponivel;
    long reservado;
    int direito;
    int gozado;
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

      direito = direitoAnual;
      gozado = gozadoAnual;
      disponivel = direitoAnual - gozadoAnual;
      reservado = faltaEntityRepository
          .countDiasPendentesDeducaoFeriasNoAno(funcionario.getUuid(), ano, excluir);
    } else {
      // Calcula o saldo total acumulado
      final var direitoTotal = feriasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());
      final var gozadoTotal = feriasGozadasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());

      direito = direitoTotal;
      gozado = gozadoTotal;
      disponivel = direitoTotal - gozadoTotal;
      reservado = faltaEntityRepository
          .countDiasPendentesDeducaoFerias(funcionario.getUuid(), excluir);
    }

    // A reserva desconta, mas nunca faz o saldo passar a negativo: é uma intenção, não um
    // consumo. Um saldo que já estava negativo antes da reserva (direito menor do que o
    // gozado, que existe em dados antigos) fica como estava — corrigi-lo aqui seria esconder
    // um problema de dados atrás de um cálculo novo.
    int saldo = disponivel <= 0
        ? disponivel
        : Math.max(0, disponivel - (int) reservado);

    return new SaldoFeriasDetalhe(
        ano, ano != null ? "ANUAL" : "ACUMULADO",
        direito, gozado, (int) reservado, saldo);
  }

  /**
   * Parcelas do saldo de ferias.
   *
   * @param direito   dias a que o colaborador tem direito
   * @param gozado    dias ja consumidos por pedidos aprovados
   * @param reservado dias comprometidos por faltas ainda em despacho (ver {@link #detalhe})
   * @param saldo     o liquido: direito - gozado - reservado, nunca abaixo de zero por causa
   *                  da reserva (um saldo ja negativo por dados antigos mantem-se negativo)
   */
  public record SaldoFeriasDetalhe(
      Integer anoReferencia, String ambito,
      int direito, int gozado, int reservado, int saldo) {
  }

  public int getSaldo(UUID funcionarioId, Integer ano) {
    return getSaldo(funcionarioId, ano, null);
  }

  public int getSaldo(UUID funcionarioId) {
    return getSaldo(funcionarioId, null, null);
  }
}
