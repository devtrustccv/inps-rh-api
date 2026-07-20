package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.domain.repository.ICalcularSubstituicaoRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamVinculoMovimentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubstituicaoWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SubstituicaoEntityRepository substituicaoEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;
  private final ICalcularSubstituicaoRepository calcularSubstituicaoRepository;
  private final SubstituicaoDetalheEntityRepository substituicaoDetalheEntityRepository;

  @Transactional
  public SubstituicaoDTO registrar(RegistarSubstituicaoCommand command) {

    var dto = command.getSubstituicao();

    if (dto.getColaboradorSubstituido() == null)
      throw IgrpResponseStatusException.badRequest("É obrigatório indicar o colaborador substituído.");

    // Inversão (UX): o dono do dossiê (idFuncionario) é o SUBSTITUTO (quem substitui); o campo
    // colaboradorSubstituido é o SUBSTITUÍDO (quem vai ser substituído).
    var idFuncionarioSubstituto = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituto não encontrado.")
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(dto.getColaboradorSubstituido()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituído não encontrado.")
    );

    // Guard: não permitir registar nova substituição enquanto o SUBSTITUTO tiver uma pendente de
    // validação (a validação vive no substituto). Evita múltiplas substituições pendentes e obriga
    // a resolver a anterior primeiro.
    if (funcionarioRules.temValidacaoPendente(funcionarioSubstituto.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO)) {
      throw IgrpResponseStatusException.badRequest(
          "O colaborador substituto já tem uma substituição pendente de validação. "
              + "Valide ou rejeite essa substituição antes de registar uma nova.");
    }

    var substitutoTiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid());
    var substituidoTiprel = funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid());

    var substituicao = new SubstituicaoEntity();
    substituicao.setSubstitutoTiprelId(substitutoTiprel);
    substituicao.setSubstituidoTiprelId(substituidoTiprel);
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setMotivo(ValidationUtil.trimToNull(dto.getMotivoSubstituicao()));
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setUuid(IdentificadorUnico.create().valor());
    substituicao.setEstado(Estado.P);
    substituicaoEntityRepository.save(substituicao);

    // Caso de teste / item 50-51: a substituição só segue para VALIDAÇÃO quando existe diferença
    // salarial a favor do substituto (salário do substituto < salário do substituído). Nesse caso
    // regista-se a diferença em RH_T_DEF_REMUNERACOES (Tipo Movimento com TIPO='REM_SUBSTITUICAO'
    // parametrizado no vínculo, OBS='Substituição') + RH_T_TIPREL_REM_PAG, UM registo por mês, com o
    // valor proporcional aos dias — reutilizando a regra oficial (proc CALCULAR_SUBSTITUICAO).
    var salarioSubstituto = substitutoTiprel.getSalario();
    var salarioSubstituido = substituidoTiprel.getSalario();
    boolean temDiferencaSalarial = salarioSubstituto != null && salarioSubstituido != null
        && salarioSubstituto.compareTo(salarioSubstituido) < 0;

    if (temDiferencaSalarial) {
      registarDiferencaMensal(substituicao, substitutoTiprel, funcionarioSubstituto,
          salarioSubstituto, salarioSubstituido);

      // Existe diferença → segue para validação, associada ao SUBSTITUTO (quem regista e recebe a
      // diferença). FUN_ID = substituto; a validação aparece no contexto do substituto.
      var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.SUBSTITUICAO.name(), Estado.P);
      validacao.setFunId(funcionarioSubstituto);
      validacao.setTiprelId(substituicao.getSubstitutoTiprelId());
      validacao.setReferenciaId(substituicao.getId());
      validacao.setReferenciaUuid(substituicao.getUuid());
      funcionarioSubstituto.getValidacoes().add(validacao);
    }

    funcionarioEntityRepository.save(funcionarioSubstituto);

    return dto;

  }

  @Transactional
  public SubstituicaoDTO validar(ValidarSubstituicaoCommand command) {
    var dto = command.getSubstituicao();

    var idSusbtituicao = IdentificadorUnico.from(command.getSubstituicaoId()).valor();

    if (dto.getColaboradorSubstituido() == null)
      throw IgrpResponseStatusException.badRequest("É obrigatório indicar o colaborador substituído.");

    // Inversão (UX): o dono do dossiê (idFuncionario) é o SUBSTITUTO (quem substitui); o campo
    // colaboradorSubstituido é o SUBSTITUÍDO (quem vai ser substituído).
    var idFuncionarioSubstituto = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituto não encontrado.")
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(dto.getColaboradorSubstituido()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário substituído não encontrado.")
    );

    var substituicao = substituicaoEntityRepository.findByUuid(idSusbtituicao).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Substituição não encontrada.")
    );
    // TODO(guard I/E temporariamente desativado): funcionarioRules.garantirEditavel(substituicao.getEstado());
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setSubstitutoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid()));
    substituicao.setSubstituidoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()));

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      if(estado.equals(Estado.A)){
        ordemServicoWriteService.criar(
            funcionarioSubstituido,
            funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()),
            dto.getTipoOrdemServico());
      }

      substituicao.setEstado(estado);

      // RH_T_SUBSTITUICAO_DETALHE (detalhe mensal) acompanha a decisão: P->A na aprovação, P->I na rejeição.
      substituicaoDetalheEntityRepository.findBySubstituicaoId_Id(substituicao.getId())
          .stream()
          .filter(d -> d.getEstado() == Estado.P)
          .forEach(d -> d.setEstado(estado));

      // Diferença salarial: o DEF_REMUNERACOES da diferença acompanha a decisão — P->A na aprovação,
      // P->I na rejeição. Identifica-se pelo Tipo de Movimento REM_SUBSTITUICAO do vínculo do
      // substituto (determinístico), não por texto de OBS. Sem isto a diferença ficaria pendente.
      var tmSubstituicao = tipoMovimentoSubstituicao(substituicao.getSubstitutoTiprelId());
      if (tmSubstituicao != null) {
        definicaoRemuneracaoEntityRepository
            .findByFunIdAndTmIdAndEstado(funcionarioSubstituto, tmSubstituicao, Estado.P)
            .forEach(r -> r.setEstado(estado));
      }

      funcionarioRules.getValidacaoPendente(funcionarioSubstituido.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO)
          .ifPresent(v -> v.setEstado(estado));

    }


    substituicaoEntityRepository.save(substituicao);
    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;
  }

  /**
   * Regista a diferença salarial da substituição em RH_T_DEF_REMUNERACOES — UM registo por mês do
   * período, com o valor proporcional aos dias, calculado pela regra oficial (proc
   * CALCULAR_SUBSTITUICAO via {@link ICalcularSubstituicaoRepository}), o mesmo cálculo do endpoint
   * de "calcular substituição". Cada registo fica ligado ao tiprel do substituto (RH_T_TIPREL_REM_PAG),
   * OBS='Substituição', estado P (passa a A/I na validação). Não faz nada se o vínculo não tiver o
   * Tipo de Movimento REM_SUBSTITUICAO parametrizado (já registado em WARN) ou se faltarem as datas.
   */
  private void registarDiferencaMensal(SubstituicaoEntity substituicao,
                                       TiposRelacionamentoEntity substitutoTiprel,
                                       FuncionarioEntity funcionarioSubstituto,
                                       BigDecimal salarioSubstituto, BigDecimal salarioSubstituido) {

    LocalDate dataInicio = substituicao.getDataInicio();
    LocalDate dataFim = substituicao.getDataFim();
    if (dataInicio == null || dataFim == null || dataFim.isBefore(dataInicio)) {
      log.warn("Substituição {}: datas inválidas (inicio={}, fim={}); detalhe/diferença não registados.",
          substituicao.getId(), dataInicio, dataFim);
      return;
    }

    // Tipo de Movimento da diferença (REM_SUBSTITUICAO); se não existir, o WARN é emitido e não se
    // grava DEF_REMUNERACOES, mas o RH_T_SUBSTITUICAO_DETALHE (detalhe mensal) é sempre criado.
    var tm = tipoMovimentoSubstituicao(substitutoTiprel);

    YearMonth mesAtual = YearMonth.from(dataInicio);
    YearMonth mesFim = YearMonth.from(dataFim);

    while (!mesAtual.isAfter(mesFim)) {
      LocalDate diaInicio = dataInicio.isAfter(mesAtual.atDay(1)) ? dataInicio : mesAtual.atDay(1);
      LocalDate diaFim = dataFim.isBefore(mesAtual.atEndOfMonth()) ? dataFim : mesAtual.atEndOfMonth();
      int nrDias = (int) (diaFim.toEpochDay() - diaInicio.toEpochDay() + 1);

      // proc: P_VALOR_TIPREL_DE = substituto, P_VALOR_TIPREL_PARA = substituído
      BigDecimal valorReceber = calcularSubstituicaoRepository
          .calcularValorReceber(nrDias, salarioSubstituto, salarioSubstituido);

      // Caso de uso: RH_T_SUBSTITUICAO_DETALHE — um registo por mês do período.
      var detalhe = new SubstituicaoDetalheEntity();
      detalhe.setSubstituicaoId(substituicao);
      detalhe.setMesAno(mesAtual.format(DateTimeFormatter.ofPattern("yyyyMM")));
      detalhe.setNrDias(nrDias);
      detalhe.setValorDoSubstituto(salarioSubstituto);
      detalhe.setValorDoSubstituido(salarioSubstituido);
      // Diferença salarial do mês (proporcional aos dias) = valor a favor do substituto (proc).
      detalhe.setValorDiferenca(valorReceber);
      detalhe.setEstado(Estado.P);
      substituicaoDetalheEntityRepository.save(detalhe);

      // Diferença salarial em DEF_REMUNERACOES (+ TIPREL_REM_PAG), OBS='Substituição' — só quando o
      // vínculo tem REM_SUBSTITUICAO parametrizado e há valor a favor do substituto.
      if (tm != null && valorReceber != null && valorReceber.signum() > 0) {
        var defRem = definicaoRemuneracaoMapper.createRenumeracao(
            valorReceber, tm, diaInicio, diaFim, funcionarioSubstituto, substitutoTiprel.getMoeda());
        defRem.setObs("Substituição");
        defRem.setEstado(Estado.P);
        definicaoRemuneracaoEntityRepository.save(defRem);

        var link = new TipoRelRemPagEntity();
        link.setTiprelId(substitutoTiprel);
        link.setRemId(defRem);
        tipoRelRemPagEntityRepository.save(link);
      }

      mesAtual = mesAtual.plusMonths(1);
    }
  }

  /**
   * Tipo de Movimento parametrizado no vínculo do substituto para a diferença salarial de
   * substituição (RH_T_PARAM_VINCULO_MOV, TIPO='REM_SUBSTITUICAO', estado A). Devolve null se o
   * tiprel/vínculo não estiver resolvido ou o vínculo não tiver esse movimento parametrizado.
   */
  private TipoMovimentoEntity tipoMovimentoSubstituicao(TiposRelacionamentoEntity substitutoTiprel) {
    Long vinculoId = (substitutoTiprel != null && substitutoTiprel.getContrVinculoId() != null
        && substitutoTiprel.getContrVinculoId().getVinculoId() != null)
        ? substitutoTiprel.getContrVinculoId().getVinculoId().getId() : null;
    if (vinculoId == null) {
      log.warn("Substituição: não foi possível resolver o vínculo do substituto (tiprelId={}); "
          + "diferença salarial NÃO será registada/processada.",
          substitutoTiprel != null ? substitutoTiprel.getId() : null);
      return null;
    }

    var tm = paramVinculoMovimentoEntityRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, "REM_SUBSTITUICAO", Estado.A)
        .stream()
        .map(ParamVinculoMovimentoEntity::getTmId)
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);

    if (tm == null) {
      log.warn("Substituição: vínculo {} não tem Tipo de Movimento REM_SUBSTITUICAO (estado A) "
          + "parametrizado em RH_T_PARAM_VINCULO_MOV; diferença salarial NÃO será registada/processada.",
          vinculoId);
    }
    return tm;
  }
}
