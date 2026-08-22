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
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
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
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TipoRelRemPagEntityRepository;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
  private final ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;

  /** Caso de uso: mês completo do período conta sempre como 30 dias (Fev 28 / Ago 31 → 30). */
  private static final int DIAS_MES_COMPLETO = 30;
  /** Caso de uso: só se cria DEF_REMUNERACOES se o total de dias da substituição for ≥ 15. */
  private static final int LIMIAR_DIAS_DEF = 15;

  @Transactional
  public SuccessResponseDTO registrar(RegistarSubstituicaoCommand command) {

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

    // Guard de sobreposição de datas (estados A/P — activas e pendentes de validação): não permitir
    // registar uma substituição que colida no tempo com outra já existente, tanto do lado do
    // SUBSTITUÍDO (a posição já está a ser coberta nesse período) como do SUBSTITUTO (o próprio já
    // está a substituir alguém nesse período). Sobreposição: inicio_existente <= fimNovo AND
    // fim_existente >= inicioNovo.
    validarSobreposicao(funcionarioSubstituto, funcionarioSubstituido, dto.getDataInicio(), dto.getDataFim(), null);

    // Caso de teste / item 50-51: a substituição só segue para VALIDAÇÃO quando existe diferença
    // salarial a favor do substituto (salário do substituto < salário do substituído). Sem diferença
    // não há nada a validar nem a compensar → a substituição fica logo ACTIVA (A). Com diferença fica
    // P e segue para validação, onde a diferença é registada em RH_T_DEF_REMUNERACOES (DOSSIÊ 16/07:
    // "ao validar o registo logo deve fazer um registo em RH_T_DEF_REMUNERACAO") — aqui só se cria a
    // validação pendente.
    var salarioSubstituto = substitutoTiprel.getSalario();
    var salarioSubstituido = substituidoTiprel.getSalario();
    boolean temDiferencaSalarial = salarioSubstituto != null && salarioSubstituido != null
        && salarioSubstituto.compareTo(salarioSubstituido) < 0;

    var substituicao = new SubstituicaoEntity();
    substituicao.setSubstitutoTiprelId(substitutoTiprel);
    substituicao.setSubstituidoTiprelId(substituidoTiprel);
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setMotivo(ValidationUtil.trimToNull(dto.getMotivoSubstituicao()));
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setUuid(IdentificadorUnico.create().valor());
    substituicao.setEstado(temDiferencaSalarial ? Estado.P : Estado.A);
    substituicaoEntityRepository.save(substituicao);

    // DETALHE mensal (RH_T_SUBSTITUICAO_DETALHE) criado NO REGISTO para TODOS os casos (caso de uso:
    // o Caso 1, sem diferença, também tem detalhe). O DEF_REMUNERACOES (diferença processada) só se
    // cria ao validar (SIM), a partir deste detalhe.
    registarDetalheMensal(substituicao, substitutoTiprel, salarioSubstituto, salarioSubstituido);

    if (temDiferencaSalarial) {
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

    return new SuccessResponseDTO(true, substituicao.getUuid().toString(), "Substituição registada.", List.of());

  }

  @Transactional
  public SuccessResponseDTO validar(ValidarSubstituicaoCommand command) {
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

    // CORRIGIR (checker devolve ao maker): substituição pendente P -> C e validação P -> C, SEM aplicar
    // payload. O maker corrige e reenvia por este mesmo endpoint com validar=null (C -> P). Âncora =
    // substituicao.uuid (a validação vive no SUBSTITUTO).
    if (EstadoValidacao.CORRIGIR.equals(dto.getValidar())) {
      if (substituicao.getEstado() != Estado.P
          || funcionarioRules.getValidacaoPendenteByReferenciaUuid(substituicao.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO).isEmpty()) {
        throw IgrpResponseStatusException.badRequest("Só é possível devolver para correção uma substituição pendente de validação.");
      }
      funcionarioRules.devolverParaCorrecao(substituicao.getUuid(), Estado.P, Referencia.SUBSTITUICAO);
      substituicao.setEstado(Estado.C);
      substituicaoEntityRepository.save(substituicao);
      log.info("[CORRIGIR] SUBSTITUICAO devolvida para correção (substituicao={}).", substituicao.getUuid());
      return new SuccessResponseDTO(true, substituicao.getUuid().toString(),
          "Substituição devolvida para correção.", List.of());
    }
    // Guard: substituição em correção não pode ser validada antes de reenviada pelo maker.
    if (substituicao.getEstado() == Estado.C && dto.getValidar() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Substituição em correção: não pode ser validada. Corrija e reenvie primeiro.");
    }

    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(ValidationUtil.trimToNull(dto.getObs()));
    substituicao.setSubstitutoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid()));
    substituicao.setSubstituidoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()));

    // Maker reenvia a correção (C -> P): as edições acima já foram aplicadas; reabre para validação
    // (sem consolidar — isso só na validação SIM). 'validar' é nulo aqui (garantido pelo guard acima).
    if (substituicao.getEstado() == Estado.C) {
      substituicao.setEstado(Estado.P);
      funcionarioRules.reabrirParaValidacao(substituicao.getUuid(), Referencia.SUBSTITUICAO);
      substituicaoEntityRepository.save(substituicao);
      funcionarioEntityRepository.save(funcionarioSubstituto);
      return new SuccessResponseDTO(true, substituicao.getUuid().toString(),
          "Substituição corrigida e reenviada para validação.", List.of());
    }

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      if(estado.equals(Estado.A)){
        // Re-validar sobreposição na aprovação: as datas podem ter sido alteradas no formulário de
        // validação, colidindo agora com outra substituição activa. Exclui o próprio registo.
        validarSobreposicao(funcionarioSubstituto, funcionarioSubstituido,
            substituicao.getDataInicio(), substituicao.getDataFim(), substituicao.getId());

        ordemServicoWriteService.criar(
            funcionarioSubstituido,
            funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()),
            dto.getTipoOrdemServico());

        // Caso de uso: o DETALHE mensal foi criado NO REGISTO (estado P). Ao aprovar (SIM) passa a A,
        // e a diferença PROCESSADA (RH_T_DEF_REMUNERACOES + RH_T_TIPREL_REM_PAG) cria-se AGORA a partir
        // desse detalhe (os mesmos números que foram aprovados), com o limiar dos 15 dias e o merge do
        // mês já processado. A rejeição (NAO, abaixo) reverte o detalhe P→I.
        var substitutoTiprel = substituicao.getSubstitutoTiprelId();
        substituicaoDetalheEntityRepository.findBySubstituicaoId_Id(substituicao.getId())
            .stream().filter(dd -> dd.getEstado() == Estado.P)
            .forEach(dd -> dd.setEstado(Estado.A));
        registarDiferencaDef(substituicao, substitutoTiprel, funcionarioSubstituto);
      }

      substituicao.setEstado(estado);

      // Na rejeição (NAO), garantir que eventuais registos pendentes da diferença (detalhe mensal +
      // DEF_REMUNERACOES) ficam I. Na aprovação já foram criados em A acima, logo não há P a mexer.
      if (estado == Estado.I) {
        substituicaoDetalheEntityRepository.findBySubstituicaoId_Id(substituicao.getId())
            .stream()
            .filter(d -> d.getEstado() == Estado.P)
            .forEach(d -> d.setEstado(Estado.I));

        var tmSubstituicao = tipoMovimentoSubstituicao(substituicao.getSubstitutoTiprelId());
        if (tmSubstituicao != null) {
          definicaoRemuneracaoEntityRepository
              .findByFunIdAndTmIdAndEstado(funcionarioSubstituto, tmSubstituicao, Estado.P)
              .forEach(r -> r.setEstado(Estado.I));
        }
      }

      // A validação vive no SUBSTITUTO (ver registrar(): setFunId(funcionarioSubstituto)). Procurar
      // pelo substituto — antes procurava pelo substituído e a validação nunca era encontrada,
      // ficando presa em P mesmo após aprovar.
      funcionarioRules.getValidacaoPendente(funcionarioSubstituto.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO)
          .ifPresent(v -> v.setEstado(estado));

    }


    substituicaoEntityRepository.save(substituicao);
    funcionarioEntityRepository.save(funcionarioSubstituido);

    var mensagem = EstadoValidacao.SIM.equals(dto.getValidar())
        ? "Substituição validada."
        : "Substituição rejeitada.";
    return new SuccessResponseDTO(true, substituicao.getUuid().toString(), mensagem, List.of());
  }

  /**
   * Regista a diferença salarial da substituição em RH_T_DEF_REMUNERACOES — UM registo por mês do
   * período, com o valor proporcional aos dias, calculado pela regra oficial (proc
   * CALCULAR_SUBSTITUICAO via {@link ICalcularSubstituicaoRepository}), o mesmo cálculo do endpoint
   * de "calcular substituição". Cada registo fica ligado ao tiprel do substituto (RH_T_TIPREL_REM_PAG),
   * OBS='Substituição', estado A (é chamado no validar()/SIM, portanto nasce já ativo). Não faz nada
   * se o vínculo não tiver o Tipo de Movimento REM_SUBSTITUICAO parametrizado (WARN) ou faltarem datas.
   */
  /**
   * Guard de sobreposição temporal. Bloqueia o registo se, no período [inicio, fim], já existir
   * uma substituição (estado A ou P) em que este colaborador seja o SUBSTITUÍDO (a posição já
   * coberta) ou o SUBSTITUTO (já a cobrir outra pessoa). Ignora se faltarem datas.
   */
  private void validarSobreposicao(FuncionarioEntity substituto, FuncionarioEntity substituido,
                                   LocalDate inicio, LocalDate fim, Long excludeId) {
    if (inicio == null || fim == null) {
      return;
    }
    var estados = List.of(Estado.A, Estado.P);

    // Lado do SUBSTITUÍDO: a pessoa que se quer substituir já está a ser substituída por outra
    // pessoa num período que colide. Mensagem nomeia ambos, o período e o estado (activa/pendente).
    substituicaoEntityRepository
        .findBySubstituidoTiprelId_FunId_UuidAndEstadoInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            substituido.getUuid(), estados, fim, inicio)
        .stream()
        .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
        .findFirst()
        .ifPresent(s -> {
          throw IgrpResponseStatusException.badRequest(String.format(
              "Conflito de substituição: %s já está a ser substituído(a) por %s no período de %s a %s "
                  + "(substituição %s). Escolha outras datas ou resolva essa substituição primeiro.",
              nomeDo(substituido), nomeDoTiprel(s.getSubstitutoTiprelId()),
              s.getDataInicio(), s.getDataFim(), estadoFrase(s.getEstado())));
        });

    // Lado do SUBSTITUTO: quem vai substituir já está a substituir outra pessoa num período que colide.
    substituicaoEntityRepository
        .findBySubstitutoTiprelId_FunId_UuidAndEstadoInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            substituto.getUuid(), estados, fim, inicio)
        .stream()
        .filter(s -> excludeId == null || !excludeId.equals(s.getId()))
        .findFirst()
        .ifPresent(s -> {
          throw IgrpResponseStatusException.badRequest(String.format(
              "Conflito de substituição: %s já está a substituir %s no período de %s a %s "
                  + "(substituição %s). Escolha outras datas ou resolva essa substituição primeiro.",
              nomeDo(substituto), nomeDoTiprel(s.getSubstituidoTiprelId()),
              s.getDataInicio(), s.getDataFim(), estadoFrase(s.getEstado())));
        });
  }

  private static String nomeDo(FuncionarioEntity f) {
    return (f != null && f.getNome() != null && !f.getNome().isBlank()) ? f.getNome() : "o colaborador";
  }

  private static String nomeDoTiprel(TiposRelacionamentoEntity t) {
    return (t != null) ? nomeDo(t.getFunId()) : "o colaborador";
  }

  /** Descrição amigável do estado do conflito para a mensagem ao utilizador. */
  private static String estadoFrase(Estado e) {
    if (e == Estado.A) {
      return "já activa";
    }
    if (e == Estado.P) {
      return "pendente de validação";
    }
    return e != null ? e.getDescription() : "";
  }

  /** Reparte o período por mês: datas efetivas no mês, dias (mês completo = 30) e valor a favor do
   *  substituto (proc CALCULAR_SUBSTITUICAO, que escala linear em nrDias). */
  private List<MesSubstituicao> repartirPorMes(LocalDate dataInicio, LocalDate dataFim,
      BigDecimal salarioSubstituto, BigDecimal salarioSubstituido) {
    var meses = new ArrayList<MesSubstituicao>();
    if (dataInicio == null || dataFim == null || dataFim.isBefore(dataInicio)) return meses;
    YearMonth mesAtual = YearMonth.from(dataInicio);
    YearMonth mesFim = YearMonth.from(dataFim);
    while (!mesAtual.isAfter(mesFim)) {
      LocalDate diaInicio = dataInicio.isAfter(mesAtual.atDay(1)) ? dataInicio : mesAtual.atDay(1);
      LocalDate diaFim = dataFim.isBefore(mesAtual.atEndOfMonth()) ? dataFim : mesAtual.atEndOfMonth();
      // Mês INTEIRAMENTE dentro do período conta 30 dias (Fev 28 / Ago 31 → 30); parcial usa dias reais.
      boolean mesCompleto = diaInicio.equals(mesAtual.atDay(1)) && diaFim.equals(mesAtual.atEndOfMonth());
      int nrDias = mesCompleto ? DIAS_MES_COMPLETO : (int) (diaFim.toEpochDay() - diaInicio.toEpochDay() + 1);
      BigDecimal valor = calcularSubstituicaoRepository.calcularValorReceber(nrDias, salarioSubstituto, salarioSubstituido);
      meses.add(new MesSubstituicao(mesAtual, diaInicio, diaFim, nrDias, valor));
      mesAtual = mesAtual.plusMonths(1);
    }
    return meses;
  }

  /**
   * DETALHE mensal (RH_T_SUBSTITUICAO_DETALHE) — criado NO REGISTO, para TODOS os casos (caso de uso:
   * o Caso 1, sem diferença, também tem detalhe). Mês completo conta 30 dias. A diferença mostrada é 0
   * quando não há valor a favor do substituto (substituto ganha ≥). O estado segue a substituição
   * (P se vai a validação; A se não).
   */
  private void registarDetalheMensal(SubstituicaoEntity substituicao, TiposRelacionamentoEntity substitutoTiprel,
      BigDecimal salarioSubstituto, BigDecimal salarioSubstituido) {
    LocalDate dataInicio = substituicao.getDataInicio();
    LocalDate dataFim = substituicao.getDataFim();
    if (dataInicio == null || dataFim == null || dataFim.isBefore(dataInicio)) {
      log.warn("Substituição {}: datas inválidas (inicio={}, fim={}); detalhe não registado.",
          substituicao.getId(), dataInicio, dataFim);
      return;
    }
    for (var m : repartirPorMes(dataInicio, dataFim, salarioSubstituto, salarioSubstituido)) {
      BigDecimal diferenca = m.valor() != null && m.valor().signum() > 0 ? m.valor() : BigDecimal.ZERO;
      var detalhe = new SubstituicaoDetalheEntity();
      detalhe.setSubstituicaoId(substituicao);
      detalhe.setMesAno(m.mes().format(DateTimeFormatter.ofPattern("yyyyMM")));
      detalhe.setNrDias(m.nrDias());
      detalhe.setValorDoSubstituto(salarioSubstituto);
      detalhe.setValorDoSubstituido(salarioSubstituido);
      detalhe.setValorDiferenca(diferenca);
      detalhe.setEstado(substituicao.getEstado());
      substituicaoDetalheEntityRepository.save(detalhe);
    }
  }

  /**
   * DEF_REMUNERACOES (+ TIPREL_REM_PAG) da diferença — criado AO VALIDAR (SIM), a partir do DETALHE já
   * gravado no registo (mesmos números que foram aprovados). Só se o TOTAL de dias ≥ 15 (Caso 2 fica
   * sem def). Merge (Caso 4.1/4.2): um mês já processado em folha não gera def próprio — a sua
   * diferença arrasta para o mês seguinte.
   */
  private void registarDiferencaDef(SubstituicaoEntity substituicao, TiposRelacionamentoEntity substitutoTiprel,
      FuncionarioEntity funcionarioSubstituto) {
    var tm = tipoMovimentoSubstituicao(substitutoTiprel);
    if (tm == null) return;

    var detalhes = substituicaoDetalheEntityRepository.findBySubstituicaoId_Id(substituicao.getId())
        .stream()
        .sorted((a, b) -> a.getMesAno().compareTo(b.getMesAno()))
        .toList();

    int totalDias = detalhes.stream().mapToInt(d -> d.getNrDias() != null ? d.getNrDias() : 0).sum();
    if (totalDias < LIMIAR_DIAS_DEF) return; // Caso 2: sem def

    LocalDate dataInicio = substituicao.getDataInicio();
    LocalDate dataFim = substituicao.getDataFim();
    BigDecimal arrastar = BigDecimal.ZERO;
    for (var d : detalhes) {
      BigDecimal valorMes = d.getValorDiferenca() != null ? d.getValorDiferenca() : BigDecimal.ZERO;
      YearMonth mes = YearMonth.parse(d.getMesAno(), DateTimeFormatter.ofPattern("yyyyMM"));

      boolean mesJaProcessado = processamentoFuncionarioRepository
          .existsByTiprel_IdAndDataReferenciaDeBetween(substitutoTiprel.getId(), mes.atDay(1), mes.atEndOfMonth());
      if (mesJaProcessado) {
        arrastar = arrastar.add(valorMes); // junta ao mês seguinte
        continue;
      }

      BigDecimal valorDef = valorMes.add(arrastar);
      arrastar = BigDecimal.ZERO;
      if (valorDef.signum() <= 0) continue;

      LocalDate diaInicio = dataInicio.isAfter(mes.atDay(1)) ? dataInicio : mes.atDay(1);
      LocalDate diaFim = dataFim.isBefore(mes.atEndOfMonth()) ? dataFim : mes.atEndOfMonth();

      var defRem = definicaoRemuneracaoMapper.createRenumeracao(
          valorDef, tm, diaInicio, diaFim, funcionarioSubstituto, substitutoTiprel.getMoeda());
      defRem.setObs("Substituição");
      defRem.setEstado(Estado.A);
      definicaoRemuneracaoEntityRepository.save(defRem);

      var link = new TipoRelRemPagEntity();
      link.setTiprelId(substitutoTiprel);
      link.setRemId(defRem);
      tipoRelRemPagEntityRepository.save(link);
    }
  }

  /** Uma fatia mensal da substituição: mês, datas efetivas no mês, dias (30 se completo) e valor
   *  a favor do substituto. */
  private record MesSubstituicao(YearMonth mes, LocalDate diaInicio, LocalDate diaFim, int nrDias, BigDecimal valor) {}

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
