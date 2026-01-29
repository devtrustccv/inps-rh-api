package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.commands.MarcarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.HoraExtraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

  @Transactional
  public Map<String, ?> marcarHoraExtra(MarcarHoraExtraCommand command) {
    var req = command.getHoraextrareq();
    if (req == null)
      throw IgrpResponseStatusException.badRequest("Dados de hora extra ausentes");
    if (req.getHoraExtra() == null || req.getHoraExtra().isEmpty())
      throw IgrpResponseStatusException.badRequest("Registos de hora extra obrigatórios");

    Long firstId = null;
    String firstUuid = null;
    int totalRegistos = 0;

    for (HoraExtraDTO dto : req.getHoraExtra()) {
      if (dto.getColaborador() == null)
        throw IgrpResponseStatusException.badRequest("Colaborador obrigatório");
      if (dto.getDataInicio() == null || dto.getDataFim() == null)
        throw IgrpResponseStatusException.badRequest("Intervalo de datas obrigatório");
      if (dto.getDataFim().isBefore(dto.getDataInicio()))
        throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");
      if (dto.getHorasDiaria() == null)
        throw IgrpResponseStatusException.badRequest("Horas diárias obrigatórias");

      var funcionario = funcionarioRepository.findByIdOrThrow(dto.getColaborador());
      var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

      var dias = expandirDias(dto.getDataInicio(), dto.getDataFim());
      for (var dia : dias) {
        var sintese = buildSinteseDia(funcionario, dia, dto.getHorasDiaria());
        sintese = sinteseRepository.save(sintese);

        var he = new HoraExtraEntity();
        he.setTiprelId(tipoRelAtual);
        he.setSinteseDiarioId(sintese);
        he.setDataInicio(dia);
        he.setDataFim(dia);
        he.setHorasDiarias(dto.getHorasDiaria());
        he.setPercentagem(dto.getPercentagemHora());
        he.setValorDiario(dto.getValorDiario());
        he.setEstado(Estado.P);
        he.setUuid(UuidCreator.getTimeOrderedEpoch());
        he = horaExtraRepository.save(he);

        if (firstId == null) {
          firstId = he.getId();
          firstUuid = he.getUuid() != null ? he.getUuid().toString() : null;
        }
        totalRegistos += 1;

        var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.HORA_EXTRA.name(),
            Estado.P);
        validacao.setFunId(funcionario);
        validacao.setTiprelId(tipoRelAtual);
        funcionario.getValidacoes().add(validacao);
        funcionarioRepository.saveAndFlush(funcionario);
        HoraExtraEntity finalHe = he;
        validacaoEntityRepository.findById(validacao.getId()).ifPresent(v -> {
          v.setReferenciaId(finalHe.getId());
          validacaoEntityRepository.save(v);
        });
      }
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", firstId);
    resp.put("uuid", firstUuid);
    resp.put("totalRegistos", totalRegistos);
    return resp;
  }

  @Transactional
  public Map<String, ?> validarHoraExtra(ValidarHoraExtraCommand command) {
    var req = command.getHoraextrareq();
    if (req == null || !StringUtils.hasText(req.getValidar()))
      throw IgrpResponseStatusException.badRequest("Campo validar é obrigatório");
    if (!StringUtils.hasText(command.getHoraExtraId()))
      throw IgrpResponseStatusException.badRequest("Identificador da hora extra é obrigatório");

    Long horaExtraId;
    try {
      horaExtraId = Long.parseLong(command.getHoraExtraId());
    } catch (NumberFormatException e) {
      throw IgrpResponseStatusException.badRequest("Identificador da hora extra inválido");
    }

    var horaExtra = horaExtraRepository.findByIdOrThrow(horaExtraId);
    var tipoRel = horaExtra.getTiprelId();
    var funcionario = tipoRel != null ? tipoRel.getFunId() : null;

    if (funcionario == null)
      throw IgrpResponseStatusException.badRequest("Registo sem colaborador associado");

    var ev = EstadoValidacao.fromCodeOrThrow(req.getValidar());
    var estado = ev.equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

    horaExtra.setEstado(estado);
    horaExtraRepository.save(horaExtra);

    funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.HORA_EXTRA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    if (Estado.A.equals(estado)) {
      var horas = horaExtra.getHorasDiarias() != null ? horaExtra.getHorasDiarias() : 0;
      if (horas > 0) {
        BigDecimal valorHora;
        if (horaExtra.getValorDiario() != null && horaExtra.getValorDiario() > 0) {
          valorHora = BigDecimal.valueOf(horaExtra.getValorDiario());
        } else {
          var params = assiduidadeParametroRepository.findAllByEstado(Estado.A.getCode());
          var p = params != null && !params.isEmpty() ? params.getFirst() : null;
          var isFimDeSemana = horaExtra.getDataInicio() != null &&
              (horaExtra.getDataInicio().getDayOfWeek() == DayOfWeek.SATURDAY
                  || horaExtra.getDataInicio().getDayOfWeek() == DayOfWeek.SUNDAY);
          valorHora = p != null
              ? (isFimDeSemana ? p.getHeValorDnutil() : p.getHeValorDutil())
              : BigDecimal.ZERO;
        }
        var valor = valorHora.multiply(BigDecimal.valueOf(horas)).setScale(2, RoundingMode.HALF_UP);
        if (horaExtra.getPercentagem() != null && horaExtra.getPercentagem() > 0) {
          valor = valor.multiply(BigDecimal.valueOf(horaExtra.getPercentagem()))
              .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        var tm = tipoMovimentoHelper.getTipoMovimentoEntityHoraExtra();
        var defRem = definicaoRemuneracaoMapper.createRenumeracao(
            valor,
            tm,
            horaExtra.getDataInicio(),
            horaExtra.getDataFim(),
            funcionario,
            tipoRel.getMoeda());
        defRem.setEstado(Estado.A);
        defRem = definicaoRemuneracaoRepository.save(defRem);
        horaExtra.setDefRemId(defRem);
        horaExtraRepository.save(horaExtra);
      }
    }

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", horaExtra.getId());
    resp.put("uuid", horaExtra.getUuid() != null ? horaExtra.getUuid().toString() : null);
    resp.put("estado", horaExtra.getEstado() != null ? horaExtra.getEstado().name() : null);
    return resp;
  }

  private List<LocalDate> expandirDias(LocalDate inicio, LocalDate fim) {
    var dias = new ArrayList<LocalDate>();
    var d = inicio;
    while (!d.isAfter(fim)) {
      dias.add(d);
      d = d.plusDays(1);
    }
    return dias;
  }

  private AssiduidadeSinteseDiarioEntity buildSinteseDia(FuncionarioEntity fun, LocalDate dia, Integer horasExtra) {
    var e = new AssiduidadeSinteseDiarioEntity();
    e.setFuncionarioId(fun);
    e.setData(dia);
    e.setMes(dia.getMonthValue());
    e.setAno(dia.getYear());
    e.setHorasExtras(formatHoras(horasExtra));
    e.setEstado(null);
    return e;
  }

  private String formatHoras(Integer horas) {
    if (horas == null || horas < 0) return "00:00";
    var h = horas;
    var hh = h < 10 ? "0" + h : String.valueOf(h);
    return hh + ":00";
  }

  }
