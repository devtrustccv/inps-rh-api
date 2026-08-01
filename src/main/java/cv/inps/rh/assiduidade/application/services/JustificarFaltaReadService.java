package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaByPedidoQuery;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  private final AssiduidadeSinteseDiarioEntityRepository assiduidadeSinteseDiarioEntityRepository;
  private final PedidoEntityRepository pedidoRepository;
  private final DocumentoMapper documentoMapper;

  /**
   * Rótulo do estado da falta para o resumo. {@code I} lê-se "Rejeitada" e não
   * "Inactiva" — neste ecrã o estado inactivo resulta de o RH ter recusado a
   * justificação.
   */
  private static String descreverEstadoFalta(Estado estado) {
    if (estado == null)
      return "Por justificar";
    return switch (estado) {
      case P -> "Pendente";
      case A -> "Justificada";
      case I -> "Rejeitada";
      default -> estado.getDescription();
    };
  }

  @Transactional(readOnly = true)
  public JustificarFaltaDTO getFaltaJustificadaResumo(GetJustificacaoFaltaQuery query) {

    UUID funcUuid;
    try {
      funcUuid = UUID.fromString(query.getFuncionarioId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("Funcionario UUID inválido");
    }

    // Buscar funcionário
    FuncionarioEntity funcionario = funcionarioRepository.findByUuid(funcUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Funcionário não encontrado para UUID: " + funcUuid));

    // Calcular intervalo do mês
    LocalDate inicioMes = LocalDate.of(query.getAno(), query.getMes(), 1);
    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

    // Buscar todas as sínteses diárias do funcionário no mês
    List<AssiduidadeSinteseDiarioEntity> sinteses = assiduidadeSinteseDiarioEntityRepository
        .findAllByFuncionarioIdAndDataBetween(funcionario, inicioMes, fimMes);

    // Faltas já registadas no período, indexadas pela síntese que as originou.
    // Sem isto o resumo não conseguiria mostrar o estado de cada dia
    // (Pendente / Justificada / Rejeitada).
    Map<Long, FaltaEntity> faltaPorSintese = faltaRepository
        .findAllByFuncionarioAndPeriodo(funcUuid, inicioMes, fimMes)
        .stream()
        .filter(f -> f.getSinteseDiarioId() != null)
        .collect(Collectors.toMap(
            f -> f.getSinteseDiarioId().getId(),
            Function.identity(),
            (a, b) -> a));

    List<FaltaItemDTO> itensFalta = sinteses.stream().map(s -> {
      FaltaItemDTO item = new FaltaItemDTO();
      item.setId(s.getId());
      item.setData(s.getData().toString());
      item.setHorasAusencia(s.getHorasAusencia());

      var falta = faltaPorSintese.get(s.getId());
      if (falta != null) {
        item.setEstado(falta.getEstado() != null ? falta.getEstado().getCode() : null);
        item.setEstadoDesc(descreverEstadoFalta(falta.getEstado()));
        item.setMotivo(falta.getDescricaoMotivo());
        item.setComJustificativo(falta.getFlgJustificativo());
        item.setTipoFalta(falta.getParamSitId() != null ? falta.getParamSitId().getNome() : null);
        item.setValorAusencia(falta.getValor() != null ? falta.getValor().intValue() : null);
      } else {
        // Dia com ausência mas ainda sem pedido de justificação.
        item.setEstadoDesc("Por justificar");
      }
      return item;
    }).toList();

    // Montar DTO principal
    JustificarFaltaDTO dto = new JustificarFaltaDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);
    dto.setAno(query.getAno());
    dto.setMes(query.getMes());

    return dto;
  }

  @Transactional(readOnly = true)
  public JustificarFaltaDTO getFaltaJustificada(GetJustificacaoFaltaByPedidoQuery query) {

    if (query == null || !StringUtils.hasText(query.getPedidoId())) {
      throw IgrpResponseStatusException.badRequest("Identificador do pedido é obrigatório");
    }
    // Converter UUID
    UUID pedidoUuid;
    try {
      pedidoUuid = UUID.fromString(query.getPedidoId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("UUID do pedido inválido");
    }

    // Buscar pedido
    var pedido = pedidoRepository.findByUuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Pedido não encontrado com UUID: " + pedidoUuid));

    var funcionario = pedido.getFunId();

    // Buscar todas as faltas associadas ao pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);



    // Mapear para FaltaItemDTO
    List<FaltaItemDTO> itensFalta = faltas.stream().map(f -> {
      var item = new FaltaItemDTO();
      item.setId(f.getSinteseDiarioId().getId());
      item.setData(f.getSinteseDiarioId().getData().toString());
      // item.setTipoFalta(f.getParamSitId() != null ? f.getParamSitId().getNome() :
      // null);
      item.setValorAusencia(null); // calculo futuro
      item.setHorasAusencia(f.getHorasAusencia());
      item.setMotivo(f.getDescricaoMotivo());
      item.setComJustificativo(f.getFlgJustificativo());

      List<DocumentoEntity> documentos = documentoEntityRepository
          .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_FALTA.name(), f.getUuid());

      if (!documentos.isEmpty()) {
        DocumentoEntity doc = documentos.getFirst(); // Pegando o primeiro documento como exemplo
        AnexoReqDTO anexo = new AnexoReqDTO();
        anexo.setId(doc.getId());
        anexo.setTipoDocumentoId(doc.getTpDocumentoId() != null ? doc.getTpDocumentoId().getId() : null);
        anexo.setDocumento(doc.getUrl());
        item.setDocumento(anexo);
      }

      return item;
    }).toList();

    // Montar DTO principal
    var dto = new JustificarFaltaDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);

    // Campos de decisão, despacho e tipoJustificacao podem ser preenchidos a partir
    // da primeira falta
    if (!faltas.isEmpty()) {
      var primeira = faltas.getFirst();
      dto.setParecerResponsavel(primeira.getDecisaoResponsavel());
      dto.setResponsavelId(primeira.getResponsavelId() != null ? primeira.getResponsavelId().getId() : null);
      dto.setObsResponsavel(primeira.getObsResponsavel());
      dto.setDespachoRh(primeira.getDespachoRh());
      dto.setTipoJustificacao(primeira.getParamSitId() != null ? primeira.getParamSitId().getId() : null);
    }

    return dto;
  }

}
