package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaByPedidoQuery;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  private final AssiduidadeSinteseDiarioEntityRepository assiduidadeSinteseDiarioEntityRepository;
  private final PedidoEntityRepository pedidoRepository;

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
            "Funcionário não encontrado para UUID: " + funcUuid
        ));

    // Calcular intervalo do mês
    LocalDate inicioMes = LocalDate.of(query.getAno(), query.getMes(), 1);
    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

    // Buscar todas as sínteses diárias do funcionário no mês
    List<AssiduidadeSinteseDiarioEntity> sinteses =
        assiduidadeSinteseDiarioEntityRepository.findAllByFuncionarioIdAndDataBetween(funcionario, inicioMes, fimMes);

    // Mapear para FaltaItemDTO
    List<FaltaItemDTO> itensFalta = sinteses.stream().map(s -> {
      FaltaItemDTO item = new FaltaItemDTO();
      item.setId(s.getId());
      item.setData(s.getData().toString());
      // Se houver falta total (0 = falta total?), definir tipoFalta
      //item.setTipoFalta(s.getFalta() != null && s.getFalta() > 0 ? "INJUSTIFICADA" : null);
      item.setHorasAusencia(s.getHorasAusencia());
      item.setValorAusencia(null); // cálculo futuro
      item.setMotivo(null); // não há motivo na síntese
      item.setComJustificativo(null); // será preenchido quando houver falta vinculada
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
            "Pedido não encontrado com UUID: " + pedidoUuid
        ));

    var funcionario = pedido.getFunId();

    // Buscar todas as faltas associadas ao pedido
    List<FaltaEntity> faltas = faltaRepository.findAllByPedidoId(pedido);

    // Mapear para FaltaItemDTO
    List<FaltaItemDTO> itensFalta = faltas.stream().map(f -> {
      var item = new FaltaItemDTO();
      item.setId(f.getSinteseDiarioId().getId());
      item.setData(f.getSinteseDiarioId().getData().toString());
      //item.setTipoFalta(f.getParamSitId() != null ? f.getParamSitId().getNome() : null);
      item.setValorAusencia(null); // calculo futuro
      item.setHorasAusencia(f.getHorasAusencia());
      item.setMotivo(f.getDescricaoMotivo());
      item.setComJustificativo(f.getFlgJustificativo());
      return item;
    }).toList();

    // Montar DTO principal
    var dto = new JustificarFaltaDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);

    // Campos de decisão, despacho e tipoJustificacao podem ser preenchidos a partir da primeira falta
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
