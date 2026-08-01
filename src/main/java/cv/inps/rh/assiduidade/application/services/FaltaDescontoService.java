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
import java.time.LocalDate;
import java.util.Objects;

/**
 * Aplica os descontos decorrentes da validação de uma falta.
 *
 * <p>São três descontos independentes entre si:
 *
 * <ul>
 *   <li><strong>Salário</strong> — quando {@code RH_T_PARAM_SITUACAO.FLG_FALTA_DECONTO_SAL = 1}.
 *       Grava em {@code RH_T_DEF_PAGAMENTOS}, associa em {@code RH_T_TIPREL_REM_PAG.PAG_ID} e
 *       actualiza {@code RH_T_FALTA.DEF_PAG_ID} — exactamente o que
 *       {@code RH_PROCESSAMENTO_SALARIAL_DB.GRAVA_REMUN_PAG(P_REM_PAG => 'PAG')} faz do lado
 *       da BD, e o que {@code PROCESSA_FALTA} espera encontrar.</li>
 *   <li><strong>Férias</strong> — quando "Deduzir Falta Em" = {@code FERIAS}.
 *       Grava em {@code RH_T_FERIAS_GOZADAS}, validando primeiro se há saldo.</li>
 *   <li><strong>Dispensa</strong> — quando "Deduzir Falta Em" = {@code DISPENSA}.
 *       Grava em {@code RH_T_DISPENSA}.</li>
 * </ul>
 *
 * <p>O destino do desconto (férias vs dispensa) passou a ser escolha explícita do RH
 * através de {@code RH_T_FALTA.FLG_DESCONTO_FALTA}; antes era inferido de
 * {@code RH_T_PARAM_SITUACAO.TIPO_AUSENCIA}, o que adivinhava a intenção.
 */
@Service
@RequiredArgsConstructor
public class FaltaDescontoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FaltaDescontoService.class);

  /** Tipo de movimento parametrizado para o desconto de falta no salário. */
  private static final String TIPO_MOV_PAG_FALTA = "PAG_FALTA";

  /** Valor de RH_T_DEF_PAGAMENTOS.TIPO — usado por DELETE_ASSIDUIDADE para limpar. */
  private static final String TIPO_PAGAMENTO_FALTA = "FALTA";

  private static final String MOEDA_PADRAO = "CVE";

  /** Acima deste número de dias — e só com desconto salarial — a falta vai a validação. */
  private static final int LIMITE_DIAS_SEM_VALIDACAO = 3;

  /** RH_T_FALTA.TIPO. */
  public static final String TIPO_FALTA = "FALTA";

  private final DefPagamentoEntityRepository defPagamentoRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoRepository;
  private final FeriasGozadasEntityRepository feriasGozadasRepository;
  private final DispensaEntityRepository dispensaRepository;
  private final AnoEntityRepository anoRepository;
  private final SaldoFeriaService saldoFeriaService;

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

    if (descontaSalario(falta))
      aplicarDescontoSalario(falta, funcionario, tipoRel);

    var deducao = TipoDescontoFalta.fromCode(falta.getFlgDescontoFalta()).orElse(null);

    if (deducao == TipoDescontoFalta.FERIAS)
      aplicarDescontoFerias(falta, pedido, funcionario);
    else if (deducao == TipoDescontoFalta.DISPENSA)
      aplicarDescontoDispensa(falta, pedido, tipoRel);
  }

  /** @return true se o tipo de justificação implica desconto no salário. */
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
   */
  public boolean requerValidacao(int totalDias, ParamSituacaoEntity paramSituacao) {
    return totalDias > LIMITE_DIAS_SEM_VALIDACAO
        && paramSituacao != null
        && Objects.equals(paramSituacao.getFlgFaltaDecontoSal(), 1);
  }

  // ------------------------------------------------------------------

  private void aplicarDescontoSalario(
      FaltaEntity falta, FuncionarioEntity funcionario, TiposRelacionamentoEntity tipoRel) {

    var tipoMovimento = resolverTipoMovimentoFalta(tipoRel);

    var valor = falta.getValor() != null ? falta.getValor() : BigDecimal.ZERO;

    var pagamento = new DefPagamentoEntity();
    pagamento.setTmId(tipoMovimento);
    pagamento.setValor(valor);
    pagamento.setDataInicio(falta.getDataInicio().toLocalDate());
    pagamento.setDataFim(falta.getDataFim().toLocalDate());
    pagamento.setEstado(Estado.A);
    pagamento.setFunId(funcionario);
    pagamento.setTipo(TIPO_PAGAMENTO_FALTA);
    pagamento.setMoeda(MOEDA_PADRAO);
    pagamento.setObs("Falta registada referente a " + falta.getDataInicio().toLocalDate());
    pagamento.setUuid(UuidCreator.getTimeOrderedEpoch());
    pagamento = defPagamentoRepository.save(pagamento);

    var associacao = new TipoRelRemPagEntity();
    associacao.setTiprelId(tipoRel);
    associacao.setPagId(pagamento);
    tipoRelRemPagRepository.save(associacao);

    falta.setFlgDescontoSal(1);
    falta.setDefPagId(pagamento);
  }

  /**
   * Tipo de movimento do desconto de falta para o vínculo do colaborador. Filtra por
   * estado activo — há parametrizações eliminadas ('E') em BD que não devem ser usadas.
   */
  private TipoMovimentoEntity resolverTipoMovimentoFalta(TiposRelacionamentoEntity tipoRel) {

    if (tipoRel.getContrVinculoId() == null || tipoRel.getContrVinculoId().getVinculoId() == null)
      throw IgrpResponseStatusException.badRequest(
          "Colaborador sem vínculo contratual associado — não é possível apurar o desconto da falta");

    var vinculoId = tipoRel.getContrVinculoId().getVinculoId().getId();

    var movimentos = paramVinculoMovimentoRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, TIPO_MOV_PAG_FALTA, Estado.A);

    if (movimentos == null || movimentos.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "Não existe tipo de movimento '" + TIPO_MOV_PAG_FALTA + "' activo parametrizado para o vínculo "
              + vinculoId + ". Configure-o em RH_T_PARAM_VINCULO_MOV antes de validar faltas com desconto salarial.");

    return movimentos.getFirst().getTmId();
  }

  private void aplicarDescontoFerias(
      FaltaEntity falta, PedidoEntity pedido, FuncionarioEntity funcionario) {

    var dataInicio = falta.getDataInicio().toLocalDate();
    var dataFim = falta.getDataFim().toLocalDate();
    int numDias = (int) (dataFim.toEpochDay() - dataInicio.toEpochDay()) + 1;

    int saldo = saldoFeriaService.getSaldo(funcionario.getUuid());
    if (numDias > saldo)
      throw IgrpResponseStatusException.badRequest(
          "Não é possível deduzir a falta nas férias: o colaborador tem " + saldo
              + " dia(s) por gozar e seriam necessários " + numDias + ".");

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
  }

  private void aplicarDescontoDispensa(
      FaltaEntity falta, PedidoEntity pedido, TiposRelacionamentoEntity tipoRel) {

    var horasAusencia = TimeUtils.intervalFormatToHHmm(falta.getHorasAusencia());

    var dispensa = new DispensaEntity();
    dispensa.setPedidoId(pedido);
    dispensa.setTiprelId(tipoRel);
    dispensa.setData(falta.getDataInicio().toLocalDate());
    dispensa.setHoraInicio(TimeUtils.hhmmToIntervalFormat("00:00"));
    dispensa.setHoraFim(TimeUtils.hhmmToIntervalFormat(horasAusencia));
    dispensa.setTotalHora(TimeUtils.hhmmToMinutes(horasAusencia));
    dispensa.setDescricaoMotivo(falta.getDescricaoMotivo());
    dispensa.setEstado(Estado.A);
    dispensa.setUuid(UuidCreator.getTimeOrderedEpoch());
    dispensaRepository.save(dispensa);
  }

  private AnoEntity resolverAno(LocalDate data) {
    return anoRepository.findByAno(String.valueOf(data.getYear()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Ano de referência " + data.getYear() + " não encontrado em RH_T_ANO"));
  }
}
