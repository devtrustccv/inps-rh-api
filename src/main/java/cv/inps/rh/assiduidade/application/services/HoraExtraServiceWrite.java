package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HoraExtraServiceWrite {

  private static final Logger LOGGER = LoggerFactory.getLogger(HoraExtraServiceWrite.class);

  /** Tipo de movimento parametrizado para a remuneração de hora extra. */
  private static final String TIPO_MOV_REM_HORA = "REM_HORA";

  private static final String MOEDA_PADRAO = "CVE";

  /** Valor de RH_T_DEF_REMUNERACOES.TIPO — usado por DELETE_ASSIDUIDADE para limpar. */
  private static final String TIPO_REMUNERACAO_HORA_EXTRA = "HORA_EXTRA";

  private final HoraExtraEntityRepository horaExtraRepository;
  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoRepository;
  private final TipoRelRemPagEntityRepository tipoRelRemPagRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final AssiduidadeParametroEntityRepository assiduidadeParametroRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final PedidoEntityRepository pedidoRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DocumentoMapper documentoMapper;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final JdbcTemplate jdbcTemplate;

  @Transactional
  public Map<String, ?> marcarHoraExtra(MarcarHoraExtraCommand command) {

    var req = command.getHoraextrareq();
    if (req == null || req.getHoraExtra() == null || req.getHoraExtra().isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Dados de hora extra ausentes");
    }

    var pedido = new PedidoEntity();
    pedido.setEstado(Estado.P.name());
    pedido.setTipoPedido(Referencia.HORA_EXTRA.name());
    pedido.setUuid(UuidCreator.getTimeOrderedEpoch());
    pedido.setOrigem("RH");
    pedido.setEtapa("VALIDACAO");
    pedido = pedidoRepository.saveAndFlush(pedido);

    Long firstHoraExtraId = null;
    String pedidoUuid = pedido.getUuid().toString();
    int totalRegistos = 0;

    for (var dto : req.getHoraExtra()) {

      if (dto.getColaborador() == null)
        throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");

      if (dto.getDataInicio() == null || dto.getDataFim() == null)
        throw IgrpResponseStatusException.badRequest("Intervalo de datas obrigatório");

      if (dto.getDataFim().isBefore(dto.getDataInicio()))
        throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

      if (dto.getHorasDiaria() == null)
        throw IgrpResponseStatusException.badRequest("Horas diárias obrigatórias");

      var funcionario = funcionarioRepository.findByUuidOrThrow(dto.getColaborador());

      var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      if (tipoRelAtual == null)
        throw IgrpResponseStatusException.badRequest("Tipo de relacionamento atual do colaborador inválido");

      var sintese = buildSinteseDia(funcionario, dto.getDataInicio(), dto.getHorasDiaria());
      sintese = sinteseRepository.save(sintese);

      BigDecimal valorDiario = calcularValorHoraExtra(
          tipoRelAtual.getId(), dto.getDataInicio(), dto.getDataFim(),
          dto.getPercentagemReferente(), dto.getHorasDiaria());

      var he = new HoraExtraEntity();
      he.setPedidoId(pedido);
      he.setTiprelId(tipoRelAtual);
      he.setSinteseDiarioId(sintese);
      he.setDataInicio(dto.getDataInicio());
      he.setDataFim(dto.getDataFim());
      he.setHorasDiarias(dto.getHorasDiaria());
      he.setPercentagemReferente(dto.getPercentagemReferente());
      he.setValorDiario(valorDiario);
      he.setEstado(Estado.P);
      he.setUuid(UuidCreator.getTimeOrderedEpoch());
      he = horaExtraRepository.save(he);

      if (firstHoraExtraId == null)
        firstHoraExtraId = he.getId();

      if (dto.getDocumento() != null) {
        var doc = documentoMapper.toEntity(
            dto.getDocumento(),
            Estado.P,
            TableName.RH_T_HORA_EXTRA.name(),
            he.getId(),
            he.getUuid(),
            1L,
            funcionario);
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentoEntityRepository.save(doc);
      }
      totalRegistos++;
    }

    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.HORA_EXTRA.name(),
        Estado.P);
    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());
    validacaoEntityRepository.save(validacao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedidoUuid);
    resp.put("totalRegistos", totalRegistos);

    return resp;
  }

  /**
   * Valor total do período de hora extra.
   *
   * <p>Apesar do campo se chamar {@code VALOR_DIARIO}, {@code CALCULO_HORA_EXTRA}
   * devolve o somatório do <strong>período inteiro</strong> (package body, linha 2197).
   *
   * <p>Em caso de falha do procedimento cai no cálculo equivalente em Java, com registo
   * em log — antes, qualquer erro no lado Oracle derrubava o pedido do utilizador.
   */
  public BigDecimal calcularValorHoraExtra(
      Long tiprelId, LocalDate dataInicio, LocalDate dataFim,
      String percentagemReferente, Long horasDiaria) {

    try {
      var valor = jdbcTemplate.execute((ConnectionCallback<BigDecimal>) conn -> {
        try (CallableStatement cs = conn.prepareCall(
            "{ ? = call INPSRH.RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA(?, ?, ?, ?, ?) }")) {
          cs.registerOutParameter(1, Types.NUMERIC);
          cs.setLong(2, tiprelId);
          cs.setDate(3, java.sql.Date.valueOf(dataInicio));
          cs.setDate(4, java.sql.Date.valueOf(dataFim));
          cs.setString(5, percentagemReferente != null ? percentagemReferente : "DIAS_UTEIS");
          cs.setLong(6, horasDiaria != null ? horasDiaria : 0);
          cs.execute();
          return cs.getBigDecimal(1);
        }
      });

      if (valor != null && valor.signum() > 0)
        return valor.setScale(2, RoundingMode.HALF_UP);

      LOGGER.warn("CALCULO_HORA_EXTRA devolveu {} para tiprelId={} {} a {} ({}, {}h/dia)"
              + " — a usar cálculo Java.",
          valor, tiprelId, dataInicio, dataFim, percentagemReferente, horasDiaria);

    } catch (Exception e) {
      LOGGER.warn("CALCULO_HORA_EXTRA falhou para tiprelId={} {} a {} — a usar cálculo Java. Causa: {}",
          tiprelId, dataInicio, dataFim, e.getMessage(), e);
    }

    return calcularValorHoraExtraEmJava(tiprelId, dataInicio, dataFim, percentagemReferente, horasDiaria);
  }

  /**
   * Réplica em Java da fórmula de CALCULO_HORA_EXTRA (package body, linhas 2111-2196):
   * percorre os dias do período e, conforme o dia seja útil ou não, aplica a
   * percentagem respectiva sobre o valor à hora.
   */
  private BigDecimal calcularValorHoraExtraEmJava(
      Long tiprelId, LocalDate dataInicio, LocalDate dataFim,
      String percentagemReferente, Long horasDiaria) {

    var parametros = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
    if (parametros == null || parametros.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "Parametrização de assiduidade activa não encontrada — não é possível calcular a hora extra");

    var parametro = parametros.getFirst();
    int jornadaMinutos = TimeUtils.hhmmToMinutes(parametro.getDiaria());
    if (jornadaMinutos <= 0)
      throw IgrpResponseStatusException.badRequest("Jornada diária não parametrizada");

    var salario = getSalarioMensal(tiprelId);
    if (salario == null || salario.signum() <= 0) {
      LOGGER.warn("Salário indisponível para tiprelId={} — valor de hora extra fica 0", tiprelId);
      return BigDecimal.ZERO;
    }

    var jornadaHoras = BigDecimal.valueOf(jornadaMinutos)
        .divide(BigDecimal.valueOf(60), 8, RoundingMode.HALF_UP);

    // valor à hora = salário / 30 / jornada diária
    var valorHora = salario
        .divide(BigDecimal.valueOf(30), 8, RoundingMode.HALF_UP)
        .divide(jornadaHoras, 8, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(horasDiaria != null ? horasDiaria : 0));

    var pctUtil = parametro.getHeValorDutil() != null ? parametro.getHeValorDutil() : BigDecimal.ZERO;
    var pctNaoUtil = parametro.getHeValorDnutil() != null ? parametro.getHeValorDnutil() : BigDecimal.ZERO;

    String modo = percentagemReferente != null ? percentagemReferente : "DIAS_UTEIS";
    var total = BigDecimal.ZERO;

    for (var dia = dataInicio; !dia.isAfter(dataFim); dia = dia.plusDays(1)) {
      // Aproximação: fim-de-semana conta como não útil. Ao contrário de IS_DIA_UTEL,
      // não consulta RH_T_PARAM_FERIADO — um feriado em dia de semana é aqui contado
      // como útil. É o custo de o procedimento estar indisponível; por isso o caminho
      // Oracle é o preferido e a falha fica registada em log.
      boolean util = dia.getDayOfWeek().getValue() <= 5;

      BigDecimal pct = switch (modo) {
        case "DIAS_UTEIS" -> util ? pctUtil : BigDecimal.ZERO;
        case "DIAS_NAO_UTEIS" -> util ? BigDecimal.ZERO : pctNaoUtil;
        default -> util ? pctUtil : pctNaoUtil;
      };

      total = total.add(valorHora.multiply(pct).divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP));
    }

    return total.setScale(2, RoundingMode.HALF_UP);
  }

  private BigDecimal getSalarioMensal(Long tiprelId) {
    try {
      return jdbcTemplate.queryForObject(
          "SELECT salario FROM rh_t_tipos_relacionamento WHERE id = ?", BigDecimal.class, tiprelId);
    } catch (Exception e) {
      LOGGER.error("Não foi possível obter o salário do tiprelId={}: {}", tiprelId, e.getMessage());
      return null;
    }
  }

  @Transactional
  public Map<String, ?> validarHoraExtra(ValidarHoraExtraCommand command) {

    var req = command.getHoraextrareq();
    if (req == null || req.getValidar() == null)
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");

    if (!StringUtils.hasText(command.getPedidoId()))
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");

    var pedidoUuid = UUID.fromString(command.getPedidoId());
    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Pedido não encontrado"));

    var horasExtra = horaExtraRepository.findAllByPedidoId_UuidAndEstado(pedidoUuid, Estado.P);
    if (horasExtra.isEmpty())
      throw IgrpResponseStatusException.badRequest("Pedido já validado ou sem registos pendentes");

    Estado estado = Objects.equals(req.getValidar(), EstadoValidacao.SIM) ? Estado.A : Estado.I;

    var anexosExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_HORA_EXTRA.name(), pedido.getUuid());
    if (anexosExistentes != null && !anexosExistentes.isEmpty()) {
      anexosExistentes.forEach(a -> a.setEstado(estado));
      documentoEntityRepository.saveAll(anexosExistentes);
    }

    Map<LocalDate, HoraExtraDTO> ajustes = new HashMap<>();
    if (req.getHoraExtra() != null) {
      for (HoraExtraDTO dto : req.getHoraExtra()) {
        if (dto.getDataInicio() != null)
          ajustes.put(dto.getDataInicio(), dto);
      }
    }

    for (var he : horasExtra) {
      he.setEstado(estado);
      horaExtraRepository.save(he);

      // Validado ⇒ entra no processamento salarial como remuneração.
      if (estado == Estado.A)
        registarRemuneracaoHoraExtra(he);

      var ajuste = ajustes.get(he.getDataInicio());

      if (ajuste != null) {
        if (ajuste.getHorasDiaria() != null)
          he.setHorasDiarias(ajuste.getHorasDiaria());
        if (ajuste.getPercentagemReferente() != null)
          he.setPercentagemReferente(ajuste.getPercentagemReferente());

        // Recalcula valor se houve ajuste de horas ou percentagem
        if (ajuste.getHorasDiaria() != null || ajuste.getPercentagemReferente() != null) {
          BigDecimal novoValor = calcularValorHoraExtra(
              he.getTiprelId().getId(), he.getDataInicio(), he.getDataFim(),
              he.getPercentagemReferente(), he.getHorasDiarias());
          he.setValorDiario(novoValor);
        }

        if (ajuste.getDocumento() != null) {
            var docsHe = documentoEntityRepository
                .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_HORA_EXTRA.name(), he.getUuid());

            var fun = he.getTiprelId() != null ? he.getTiprelId().getFunId() : null;

            if (docsHe != null && !docsHe.isEmpty()) {
              var existing = docsHe.getFirst();
              var mapped = documentoMapper.toEntity(
                  ajuste.getDocumento(),
                  estado,
                  TableName.RH_T_HORA_EXTRA.name(),
                  he.getId(),
                  he.getUuid(),
                  1L,
                  fun);
              existing.setTpDocumentoId(mapped.getTpDocumentoId());
              existing.setUrl(mapped.getUrl());
              existing.setEstado(estado);
              documentoEntityRepository.save(existing);
            } else {
              var novo = documentoMapper.toEntity(
                  ajuste.getDocumento(),
                  estado,
                  TableName.RH_T_HORA_EXTRA.name(),
                  he.getId(),
                  he.getUuid(),
                  1L,
                  fun);
              novo.setUuid(UuidCreator.getTimeOrderedEpoch());
              documentoEntityRepository.save(novo);
            }
          }
      }
    }

    funcionarioRules.getValidacaoPendenteByReferenciaUuid(pedido.getUuid(), TipoAcao.INSERT, Referencia.HORA_EXTRA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    pedido.setEstado(estado.name());
    pedidoRepository.save(pedido);

    if (estado == Estado.A && !horasExtra.isEmpty()) {
      var tiprel = horasExtra.getFirst().getTiprelId();
      ordemServicoWriteService.criar(tiprel.getFunId(), tiprel, req.getTipoOrdemServico());
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid());
    resp.put("estado", pedido.getEstado());
    resp.put("totalRegistos", horasExtra.size());

    return resp;
  }

  /**
   * Regista a hora extra validada em {@code RH_T_DEF_REMUNERACOES} e associa em
   * {@code RH_T_TIPREL_REM_PAG.REM_ID} — o equivalente a
   * {@code GRAVA_REMUN_PAG(P_REM_PAG => 'REM')} do lado da BD, e o que
   * {@code PROCESSA_HORA} espera encontrar.
   *
   * <p>O tipo de movimento vem da parametrização {@code REM_HORA} do vínculo, filtrada
   * por estado activo — há linhas eliminadas ('E') em BD que não devem ser usadas.
   */
  private void registarRemuneracaoHoraExtra(HoraExtraEntity he) {

    var tipoRel = he.getTiprelId();
    if (tipoRel == null || tipoRel.getContrVinculoId() == null
        || tipoRel.getContrVinculoId().getVinculoId() == null)
      throw IgrpResponseStatusException.badRequest(
          "Colaborador sem vínculo contratual associado — não é possível registar a hora extra");

    var vinculoId = tipoRel.getContrVinculoId().getVinculoId().getId();

    var movimentos = paramVinculoMovimentoRepository
        .findByVinculoId_IdAndTipoAndEstado(vinculoId, TIPO_MOV_REM_HORA, Estado.A);

    if (movimentos == null || movimentos.isEmpty())
      throw IgrpResponseStatusException.badRequest(
          "Não existe tipo de movimento '" + TIPO_MOV_REM_HORA + "' activo parametrizado para o vínculo "
              + vinculoId + ". Configure-o em RH_T_PARAM_VINCULO_MOV antes de validar horas extra.");

    var valor = he.getValorDiario() != null ? he.getValorDiario() : BigDecimal.ZERO;

    var remuneracao = definicaoRemuneracaoMapper.createRenumeracao(
        valor,
        movimentos.getFirst().getTmId(),
        he.getDataInicio(),
        he.getDataFim(),
        tipoRel.getFunId(),   // FUN_ID, tal como GRAVA_REMUN_PAG
        MOEDA_PADRAO);

    // O mapper cria em estado P (serve os fluxos que ainda vão a validação). Aqui a
    // hora extra já foi validada, e GET_SALARIO_BASE / o processamento só olham para
    // remunerações com ESTADO = 'A' — em P ficaria invisível ao salário.
    remuneracao.setEstado(Estado.A);
    // DELETE_ASSIDUIDADE filtra por TIPO = 'HORA_EXTRA' (package body, linha 2683).
    remuneracao.setTipo(TIPO_REMUNERACAO_HORA_EXTRA);

    remuneracao = definicaoRemuneracaoRepository.save(remuneracao);

    var associacao = new TipoRelRemPagEntity();
    associacao.setTiprelId(tipoRel);
    associacao.setRemId(remuneracao);
    tipoRelRemPagRepository.save(associacao);

    // PROCESSA_HORA actualiza RH_T_HORA_EXTRA.DEF_REM_ID (package body, linha 2573) e
    // DELETE_ASSIDUIDADE limpa-o por aí (linha 2686). Sem este elo o registo ficaria
    // órfão do processamento.
    he.setDefRemId(remuneracao);
    horaExtraRepository.save(he);
  }

  private AssiduidadeSinteseDiarioEntity buildSinteseDia(FuncionarioEntity fun, LocalDate dia, Long horasExtra) {
    var e = new AssiduidadeSinteseDiarioEntity();
    e.setFuncionarioId(fun);
    e.setData(dia);
    e.setMes(dia.getMonthValue());
    e.setAno(dia.getYear());
    e.setHorasExtras(formatHorasToInterval(horasExtra));
    e.setEstado(Estado.A.name());
    return e;
  }

  /**
   * Converte um valor em minutos para string no formato Oracle INTERVAL "+0
   * HH:MM:00"
   */
  private String formatHorasToInterval(Long minutosTotais) {
    if (minutosTotais == null || minutosTotais < 0) {
      return "+0 00:00:00";
    }

    long hours = minutosTotais / 60;
    long minutes = minutosTotais % 60;

    // Garante dois dígitos
    String hh = hours < 10 ? "0" + hours : String.valueOf(hours);
    String mm = minutes < 10 ? "0" + minutes : String.valueOf(minutes);

    return "+0 " + hh + ":" + mm + ":00";
  }

}
