package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Serializa, por colaborador, quem consome os saldos dele — férias, horas de dispensa e o
 * desconto das faltas.
 *
 * <p>Todos esses fluxos são um ler-decidir-gravar: lê-se o saldo, decide-se o que cabe e
 * grava-se o consumo. Sem serialização, duas transacções simultâneas lêem o mesmo saldo antes
 * de qualquer uma gravar e concedem ambas — dois dias de direito podem virar quatro dias
 * gozados, e o saldo fica negativo sem que nenhum dos dois pedidos tenha feito nada de errado.
 *
 * <p>O lock é sobre a linha do colaborador, e não sobre cada tabela de saldo, por três razões:
 * é o dono comum de todos eles (um pedido de férias e uma falta deduzida em férias competem
 * pelo mesmo direito), existe sempre — ao contrário de um direito de férias, que pode não estar
 * criado —, e sendo um só lock, tomado à cabeça de cada caso de uso, não há duas ordens de
 * aquisição possíveis e portanto não há deadlock.
 *
 * <p>Complementa a reserva, não a substitui: a reserva fecha a janela de <em>dias</em> entre o
 * pedido e o despacho, este lock fecha a de <em>milissegundos</em> entre duas escritas.
 */
@Service
@RequiredArgsConstructor
public class SaldoLockService {

  private final FuncionarioEntityRepository funcionarioRepository;

  /**
   * Toma o lock do colaborador até ao fim da transacção em curso. Deve ser chamado <b>antes</b>
   * da primeira leitura de saldo do caso de uso — tomá-lo depois já não protege nada, porque a
   * leitura que interessa proteger já aconteceu.
   *
   * <p>Silencioso quando o colaborador não existe: quem trata disso é a validação do caso de
   * uso, com a mensagem própria. Falhar aqui trocaria um "colaborador não encontrado" claro por
   * um erro de infra-estrutura.
   */
  public void lockColaborador(UUID funcionarioUuid) {
    if (funcionarioUuid == null)
      return;
    funcionarioRepository.lockByUuid(funcionarioUuid);
  }
}
