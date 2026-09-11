package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.TipoDescontoFalta;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Aplica os descontos decorrentes da validação de uma falta.
 *
 * <ul>
 *   <li><strong>Férias</strong> — quando "Deduzir Falta Em" = {@code FERIAS}.
 *       Grava em {@code RH_T_FERIAS_GOZADAS}.</li>
 *   <li><strong>Dispensa</strong> — quando "Deduzir Falta Em" = {@code DISPENSA}.
 *       Grava em {@code RH_T_DISPENSA}.</li>
 *   <li><strong>Salário</strong> — quando {@code RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL = 1}.
 *       Só se <b>apura o valor</b> e grava-se em {@code RH_T_FALTA.VALOR_DESCONTO}.</li>
 * </ul>
 *
 * <p>O desconto no salário deixou de ser escrito aqui (decisão com o DBA, 11/09): quem cria a
 * {@code RH_T_DEF_REMUNERACOES}, a associação em {@code RH_T_TIPREL_REM_PAG} e preenche
 * {@code RH_T_FALTA.DEF_REM_ID} é o procedimento do processamento salarial, que lê as faltas em
 * {@code A} ainda sem {@code DEF_REM_ID}. A nossa responsabilidade é garantir o valor: o
 * procedimento não sabe o que férias ou dispensa já cobriram, por isso recebe o líquido feito.
 *
 * <p>{@code VALOR} continua a ser o <b>bruto</b> (o "Valor Total" do ecrã); {@code VALOR_DESCONTO}
 * é o que sai do vencimento. {@code null} = ainda não apurado (falta em P, I, E).
 */
@Service
@RequiredArgsConstructor
public class FaltaDescontoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FaltaDescontoService.class);

  /** Acima deste número de dias — e só com desconto salarial — a falta vai a validação. */
  private static final int LIMITE_DIAS_SEM_VALIDACAO = 3;

  /** RH_T_FALTA.TIPO. */
  public static final String TIPO_FALTA = "FALTA";

  private final FeriasGozadasEntityRepository feriasGozadasRepository;
  private final DispensaEntityRepository dispensaRepository;
  private final AnoEntityRepository anoRepository;
  private final SaldoFeriaService saldoFeriaService;
  private final DispensaHorasService dispensaHorasService;

  /**
   * Aplica todos os descontos aplicáveis a uma falta já validada.
   *
   * @param falta   falta em estado A
   * @param pedido  pedido que lhe deu origem
   * @param tipoRel vínculo activo do colaborador
   */
  public void aplicar(FaltaEntity falta, PedidoEntity pedido, TiposRelacionamentoEntity tipoRel) {

    if (falta == null || falta.getParamSitId() == null)
      return;

    var funcionario = pedido.getFunId();
    var deducao = TipoDescontoFalta.fromCode(falta.getFlgDescontoFalta()).orElse(null);

    int minutosAusencia = TimeUtils.parseHorasFlexivel(
        TimeUtils.intervalFormatToHHmm(falta.getHorasAusencia()));

    // O saldo cobre o que consegue; o vencimento paga o resto. Não são alternativas — são duas
    // fases da mesma cobrança (regra de negócio, 10/09): 4 dias de falta com 2 de saldo dão 2
    // dias gozados e 2 dias descontados. Cobre-se pela ordem cronológica, por isso os primeiros
    // dias esgotam o saldo e os seguintes vão ao vencimento.
    int minutosPorCobrir = minutosAusencia;

    if (deducao == TipoDescontoFalta.FERIAS)
      minutosPorCobrir = aplicarDescontoFerias(falta, pedido, funcionario, minutosAusencia);
    else if (deducao == TipoDescontoFalta.DISPENSA)
      minutosPorCobrir = aplicarDescontoDispensa(falta, pedido, tipoRel, minutosAusencia);

    // Desconta-se só o que o saldo não cobriu. Sem dedução, minutosPorCobrir é a ausência toda.
    // Zero (e não null) quando nada vai ao vencimento: a falta foi apurada, não há é o que cobrar.
    falta.setValorDesconto(minutosPorCobrir > 0 && descontaSalario(falta)
        ? valorPorCobrir(falta, minutosAusencia, minutosPorCobrir)
        : BigDecimal.ZERO);
  }

  /**
   * Desfaz tudo o que o {@link #aplicar} criou para esta falta. Simétrico dele, e partilhado
   * pelo Eliminar e pelo Editar — o editar não faz update dos efeitos, reverte-os e volta a
   * aplicá-los a partir do estado novo, senão trocar "Deduzir em" de FERIAS para DISPENSA
   * deixava as férias gozadas lá e criava a dispensa por cima, descontando duas vezes.
   *
   * <p>Os registos revertidos ficam em {@code E} (decisão de negócio, 10/09), o que os retira
   * dos saldos — que só contam os aprovados.
   *
   * <p>Não toca em {@code RH_T_DEF_REMUNERACOES}: só se chega aqui com {@code DEF_REM_ID} nulo,
   * porque o guard do editar/eliminar tranca o pedido logo que o processamento apanha uma falta.
   */
  public void reverter(FaltaEntity falta, PedidoEntity pedido) {

    if (falta == null)
      return;

    // Dedução em férias / dispensa: ambas presas ao pedido. Filtra-se pelo dia da falta porque
    // o pedido tem uma linha por dia e só este está a ser revertido.
    var dia = falta.getDataInicio().toLocalDate();

    var gozadas = feriasGozadasRepository.findAllByPedidoId_IdAndDataInicio(pedido.getId(), dia);
    gozadas.forEach(g -> g.setEstado(Estado.E));
    feriasGozadasRepository.saveAll(gozadas);

    var dispensas = dispensaRepository.findAllByPedidoId_IdAndDataInicio(pedido.getId(), dia);
    dispensas.forEach(d -> d.setEstado(Estado.E));
    dispensaRepository.saveAll(dispensas);

    // De volta a "não apurado": se a falta voltar a A, o aplicar apura-o de novo.
    falta.setValorDesconto(null);
  }

  /**
   * Quanto é que um conjunto de faltas <b>desconta</b> no vencimento: a soma de
   * {@code RH_T_FALTA.VALOR_DESCONTO} das faltas activas.
   *
   * <p>Não confundir com {@code RH_T_FALTA.VALOR}, que é o valor <b>bruto</b> da ausência
   * (valor diário × dias) e é o que a spec manda mostrar em "Valor Total". Quando há dedução em
   * férias ou dispensa, o saldo cobre parte e só o resto vai ao vencimento.
   */
  public static BigDecimal valorDescontado(List<FaltaEntity> faltas) {
    return faltas.stream()
        .filter(f -> Estado.A.equals(f.getEstado()))
        .map(FaltaEntity::getValorDesconto)
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Quanto e que o <b>saldo</b> (ferias ou dispensa) absorveu.
   *
   * <p>Calcula-se por dia e so nos dias que iam mesmo ser descontados: subtrair o descontado ao
   * bruto, sobre o pedido todo, dava a resposta errada para um tipo que nao desconta salario —
   * nada era cobrado e nada foi coberto, mas a subtraccao dava o bruto inteiro.
   *
   * <p>So conta faltas <b>despachadas</b> ({@code A}), <b>com deducao</b> e <b>já apuradas</b>
   * ({@code VALOR_DESCONTO} preenchido): e o unico caso em que um saldo pode ter coberto alguma
   * coisa. Uma falta sem valor apurado (anterior a esta coluna) nao aparece como coberta.
   */
  public static BigDecimal valorCoberto(List<FaltaEntity> faltas) {
    return faltas.stream()
        .filter(f -> f.getValor() != null && f.getValorDesconto() != null)
        .filter(f -> Estado.A.equals(f.getEstado()))
        .filter(f -> TipoDescontoFalta.fromCode(f.getFlgDescontoFalta()).isPresent())
        .filter(f -> f.getParamSitId() != null
            && Objects.equals(f.getParamSitId().getFlgFaltaDecontoSal(), 1))
        .map(f -> f.getValor().subtract(f.getValorDesconto()))
        .filter(v -> v.signum() > 0)
        .reduce(BigDecimal.ZERO, BigDecimal::add)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * @return true se o tipo de justificação implica desconto no salário.
   *
   * <p>A fonte é {@code RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL}, e é também de lá que sai
   * {@code RH_T_FALTA.FLG_DESCONTO_SAL}. A spec contradiz-se sobre isto: a regra dos 3 dias
   * (:494, :796) cita esta coluna, mas o campo hidden (:730) e o ecrã de falta
   * justificada/injustificada (:901) mandam buscá-la a {@code RH_T_TIPO_FALTAS} via
   * {@code RH_T_FALTA.TF_ID}.
   *
   * <p>Decidido a 11/09 ficar por {@code PARAM_SITUACAO} — é a única leitura implementável:
   * {@code RH_T_FALTA} <b>não tem</b> coluna {@code TF_ID} (não há ligação à tabela de tipos) e
   * {@code RH_T_TIPO_FALTAS} tem uma única linha, de teste e em {@code E}. Passar para lá exigiria
   * alteração de BD e carregar os dados; até isso acontecer, a outra leitura daria sempre nulo.
   */
  public boolean descontaSalario(FaltaEntity falta) {
    return falta != null
        && falta.getParamSitId() != null
        && Objects.equals(falta.getParamSitId().getFlgFaltaDecontoSal(), 1);
  }

  /**
   * Regra da especificação: "Somente deve ir para validação caso o número de falta for
   * maior que 3 dias, e caso o tipo de justificação esteja sujeito a desconto no salário
   * ({@code RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL}); caso contrário o registo fica
   * registado com estado ATIVO."
   *
   * <p>As duas condições são cumulativas: 5 dias sem desconto salarial não vão a
   * validação, e 2 dias com desconto também não.
   *
   * <p>Conta os dias <b>deste registo</b>, que é o que a spec diz ("o número de registo na
   * tabela RH_T_FALTA dependerá do número de dias de falta [...] somente deve ir para validação
   * caso o número de falta for maior que 3 dias"). Já esteve a contar as faltas vivas do mês
   * inteiro — acumulação que a spec nunca pediu, removida a 11/09.
   */
  public boolean requerValidacao(int totalDias, ParamSituacaoEntity paramSituacao) {
    return totalDias > LIMITE_DIAS_SEM_VALIDACAO
        && paramSituacao != null
        && Objects.equals(paramSituacao.getFlgFaltaDecontoSal(), 1);
  }

  // ------------------------------------------------------------------

  /**
   * Férias contam-se em DIAS, por isso o dia é coberto por inteiro ou não é coberto de todo.
   *
   * <p>Já não rejeita por saldo insuficiente: antes um pedido de 4 dias com 2 de saldo dava 400
   * e não gravava nada. Agora consome o que há e devolve o que ficou por cobrir, para o
   * vencimento pagar o resto.
   *
   * @return minutos que o saldo não cobriu (0 se cobriu o dia, a ausência toda se não havia saldo)
   */
  private int aplicarDescontoFerias(
      FaltaEntity falta, PedidoEntity pedido, FuncionarioEntity funcionario, int minutosAusencia) {

    var dataInicio = falta.getDataInicio().toLocalDate();
    var dataFim = falta.getDataFim().toLocalDate();
    int numDias = (int) (dataFim.toEpochDay() - dataInicio.toEpochDay()) + 1;

    // Saldo relido a cada dia: as férias gozadas gravadas nos dias anteriores deste mesmo
    // pedido já cá estão, e é isso que faz o saldo esgotar-se pela ordem cronológica.
    //
    // O próprio pedido é excluído da reserva: as suas faltas ainda estão em P neste
    // instante (só passam a A depois de os descontos serem aplicados) e sem a exclusão
    // reservariam contra si próprias — o pedido roubava-se a si mesmo o saldo que vem gastar.
    int saldo = saldoFeriaService.getSaldo(funcionario.getUuid(), null, pedido.getId());
    if (saldo < numDias)
      return minutosAusencia;

    var feriasGozadas = new FeriasGozadasEntity();
    feriasGozadas.setFunId(funcionario);
    feriasGozadas.setPedidoId(pedido);
    feriasGozadas.setAnoId(resolverAno(dataInicio));
    feriasGozadas.setDataInicio(dataInicio);
    feriasGozadas.setDataFim(dataFim);
    feriasGozadas.setNumDia(numDias);
    feriasGozadas.setEstado(Estado.A);
    feriasGozadas.setUuid(UuidCreator.getTimeOrderedEpoch());
    feriasGozadasRepository.save(feriasGozadas);
    return 0;
  }

  /**
   * Dispensa conta-se em HORAS, não em dias, por isso a cobertura pode ser parcial: 8h de
   * ausência com 3h de saldo consomem as 3h e deixam 5h para o vencimento. A alternativa —
   * tudo-ou-nada — desperdiçaria as 3h e faria o colaborador perder o dia inteiro.
   *
   * @return minutos que o saldo não cobriu
   */
  private int aplicarDescontoDispensa(
      FaltaEntity falta, PedidoEntity pedido, TiposRelacionamentoEntity tipoRel,
      int minutosAusencia) {

    var dia = falta.getDataInicio().toLocalDate();

    // Relido a cada dia, tal como nas férias: as dispensas gravadas nos dias anteriores do
    // mesmo pedido já contam para as horas usadas do mês. E, pela mesma razão das férias, o
    // próprio pedido fica fora da reserva — as suas faltas ainda estão em P.
    var status = dispensaHorasService.getHorasStatus(
        pedido.getFunId().getUuid(), dia, null, pedido.getId());
    int disponiveis = status.getHorasRestantesMinutos() != null
        ? status.getHorasRestantesMinutos() : 0;

    int cobertos = Math.min(disponiveis, minutosAusencia);
    if (cobertos <= 0)
      return minutosAusencia;

    var dispensa = new DispensaEntity();
    dispensa.setPedidoId(pedido);
    dispensa.setTiprelId(tipoRel);
    dispensa.setDataInicio(dia);
    dispensa.setDataFim(falta.getDataFim() != null
        ? falta.getDataFim().toLocalDate() : dia);
    dispensa.setHoraInicio(TimeUtils.hhmmToIntervalFormat("00:00"));
    dispensa.setHoraFim(TimeUtils.hhmmToIntervalFormat(TimeUtils.formatMinutesToHHmm(cobertos)));
    dispensa.setTotalHora(cobertos);
    dispensa.setDescricaoMotivo(falta.getDescricaoMotivo());
    dispensa.setEstado(Estado.A);
    dispensa.setUuid(UuidCreator.getTimeOrderedEpoch());
    dispensaRepository.save(dispensa);

    return minutosAusencia - cobertos;
  }

  /**
   * Parte do valor do dia correspondente às horas que o saldo não cobriu.
   *
   * <p>falta.valor é o valor do dia inteiro (valor à hora x horas de ausência). Quando o saldo
   * cobriu parte das horas — só acontece na dispensa, que conta em horas —, desconta-se a
   * proporção que sobrou.
   */
  private BigDecimal valorPorCobrir(FaltaEntity falta, int minutosAusencia, int minutosPorCobrir) {
    var valorDia = falta.getValor() != null ? falta.getValor() : BigDecimal.ZERO;
    if (minutosAusencia <= 0 || minutosPorCobrir >= minutosAusencia)
      return valorDia;
    return valorDia
        .multiply(BigDecimal.valueOf(minutosPorCobrir))
        .divide(BigDecimal.valueOf(minutosAusencia), 2, RoundingMode.HALF_UP);
  }

  private AnoEntity resolverAno(LocalDate data) {
    return anoRepository.findByAno(String.valueOf(data.getYear()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Ano de referência " + data.getYear() + " não encontrado em RH_T_ANO"));
  }
}
