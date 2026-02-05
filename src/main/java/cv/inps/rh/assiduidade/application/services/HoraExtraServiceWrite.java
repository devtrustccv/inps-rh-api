package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.commands.MarcarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.commands.ValidarHoraExtraCommand;
import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.PedidoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.DayOfWeek;
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
    pedido = pedidoRepository.save(pedido);

    FuncionarioEntity primeiroFuncionario = null;
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
      if (primeiroFuncionario == null) {
        primeiroFuncionario = funcionario;
        pedido.setFunId(funcionario);
        pedidoRepository.save(pedido);
      }

      var tipoRelAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
      var dias = expandirDias(dto.getDataInicio(), dto.getDataFim());

      for (var dia : dias) {

        var sintese = buildSinteseDia(funcionario, dia, dto.getHorasDiaria());
        sintese = sinteseRepository.save(sintese);

        int valorDiario = calcularValorHoraExtra(tipoRelAtual, dto.getHorasDiaria(), dto.getPercentagemHora());

        var he = new HoraExtraEntity();
        he.setPedidoId(pedido);
        he.setTiprelId(tipoRelAtual);
        he.setSinteseDiarioId(sintese);
        he.setDataInicio(dia);
        he.setDataFim(dia);
        he.setHorasDiarias(dto.getHorasDiaria());
        he.setPercentagem(dto.getPercentagemHora());
        he.setValorDiario(valorDiario);
        he.setEstado(Estado.P);
        he.setUuid(UuidCreator.getTimeOrderedEpoch());
        he = horaExtraRepository.save(he);

        if (firstHoraExtraId == null) firstHoraExtraId = he.getId();
        totalRegistos++;
      }
    }

    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty() && primeiroFuncionario != null) {
      List<DocumentoEntity> documentos = new ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            Estado.P,
            Referencia.HORA_EXTRA.name(),
            pedido.getId(),
            pedido.getUuid(),
            1L,
            primeiroFuncionario
        );
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        documentos.add(doc);
      }
      documentoEntityRepository.saveAll(documentos);
    }

    var validacao = dadosContratuaisMapper.toValidacaoInsert(
        TipoAcao.INSERT.name(),
        Referencia.HORA_EXTRA.name(),
        Estado.P
    );

    validacao.setReferenciaId(pedido.getId());
    validacao.setReferenciaUuid(pedido.getUuid());
    validacaoEntityRepository.save(validacao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedidoUuid);
    resp.put("totalRegistos", totalRegistos);

    return resp;
  }

  private int calcularValorHoraExtra(TiposRelacionamentoEntity tipoRel, int horasDiarias, Integer percentagem) {
    if (percentagem == null) percentagem = 100;
    BigDecimal salarioDiario = tipoRel.getSalario() != null ? tipoRel.getSalario() : BigDecimal.ZERO;
    BigDecimal valor = salarioDiario.multiply(BigDecimal.valueOf(horasDiarias))
        .multiply(BigDecimal.valueOf(percentagem))
        .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
    return valor.intValue();
  }

  private List<LocalDate> expandirDias(LocalDate inicio, LocalDate fim) {
    List<LocalDate> dias = new ArrayList<>();
    LocalDate d = inicio;
    while (!d.isAfter(fim)) {
      dias.add(d);
      d = d.plusDays(1);
    }
    return dias;
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

    // Anexos
    if (req.getDocumentos() != null && !req.getDocumentos().isEmpty() && pedido.getFunId() != null) {
      List<DocumentoEntity> novos = new ArrayList<>();
      for (var d : req.getDocumentos()) {
        var doc = documentoMapper.toEntity(
            d,
            estado,
            Referencia.HORA_EXTRA.name(),
            pedido.getId(),
            pedido.getUuid(),
            1L,
            pedido.getFunId()
        );
        doc.setUuid(UuidCreator.getTimeOrderedEpoch());
        novos.add(doc);
      }
      documentoEntityRepository.saveAll(novos);
    }

    var anexosExistentes = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(Referencia.HORA_EXTRA.name(), pedido.getUuid());
    if (anexosExistentes != null && !anexosExistentes.isEmpty()) {
      anexosExistentes.forEach(a -> a.setEstado(estado));
      documentoEntityRepository.saveAll(anexosExistentes);
    }

    Map<LocalDate, HoraExtraDTO> ajustes = new HashMap<>();
    if (req.getHoraExtra() != null) {
      for (HoraExtraDTO dto : req.getHoraExtra()) {
        if (dto.getDataInicio() != null) ajustes.put(dto.getDataInicio(), dto);
      }
    }

    for (var he : horasExtra) {
      var ajuste = ajustes.get(he.getDataInicio());
      if (ajuste != null) {
        if (ajuste.getHorasDiaria() != null) he.setHorasDiarias(ajuste.getHorasDiaria());
        if (ajuste.getPercentagemHora() != null) he.setPercentagem(ajuste.getPercentagemHora());
        if (ajuste.getValorDiario() != null) he.setValorDiario(ajuste.getValorDiario());
      }
      he.setEstado(estado);
      horaExtraRepository.save(he);
    }

    funcionarioRules.getValidacaoPendenteByReferenciaUuid(pedido.getUuid(), TipoAcao.INSERT, Referencia.HORA_EXTRA)
        .ifPresent(v -> {
          v.setEstado(estado);
          validacaoEntityRepository.save(v);
        });

    pedido.setEstado(estado.name());
    pedidoRepository.save(pedido);

    Map<String, Object> resp = new HashMap<>();
    resp.put("pedidoId", pedido.getId());
    resp.put("pedidoUuid", pedido.getUuid());
    resp.put("estado", pedido.getEstado());
    resp.put("totalRegistos", horasExtra.size());

    return resp;
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
    String hh = horas < 10 ? "0" + horas : String.valueOf(horas);
    return hh + ":00";
  }
}
