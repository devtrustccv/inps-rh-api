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
import lombok.RequiredArgsConstructor;
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

  private final HoraExtraEntityRepository horaExtraRepository;
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

  public BigDecimal calcularValorHoraExtra(
      Long tiprelId, LocalDate dataInicio, LocalDate dataFim,
      String percentagemReferente, Long horasDiaria) {

    return jdbcTemplate.execute((ConnectionCallback<BigDecimal>) conn -> {
      try (CallableStatement cs = conn.prepareCall(
          "{ ? = call INPSRH.RH_PROCESSAMENTO_SALARIAL_DB.CALCULO_HORA_EXTRA(?, ?, ?, ?, ?) }")) {
        cs.registerOutParameter(1, Types.NUMERIC);
        cs.setLong(2, tiprelId);
        cs.setDate(3, java.sql.Date.valueOf(dataInicio));
        cs.setDate(4, java.sql.Date.valueOf(dataFim));
        cs.setString(5, percentagemReferente != null ? percentagemReferente : "DIAS_UTEIS");
        cs.setLong(6, horasDiaria != null ? horasDiaria : 0);
        cs.execute();
        BigDecimal result = cs.getBigDecimal(1);
        return result != null ? result.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
      }
    });
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
