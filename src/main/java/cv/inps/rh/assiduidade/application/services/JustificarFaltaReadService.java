package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaQuery;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;

  @Transactional(readOnly = true)
  public JustificarFaltaDTO getFaltaJustificada(GetJustificacaoFaltaQuery query) {
    if (query == null || !StringUtils.hasText(query.getFaltaId())) {
      return new JustificarFaltaDTO();
    }
    Long id;
    try {
      id = Long.parseLong(query.getFaltaId());
    } catch (NumberFormatException e) {
      return new JustificarFaltaDTO();
    }

    var e = faltaRepository.findByIdOrThrow(id);

    var dto = new JustificarFaltaDTO();
    var pedido = e.getPedidoId();
    var fun = pedido != null ? pedido.getFunId() : null;
    dto.setColaboradorId(fun != null ? fun.getId() : null);
    dto.setNomeColaborador(fun != null ? fun.getNome() : null);

    var item = new FaltaItemDTO();
    item.setId(e.getId());
    item.setDataInicio(DateFormatter.localDateTimeToLocalDateString(e.getDataInicio()));
    item.setDataFim(DateFormatter.localDateTimeToLocalDateString(e.getDataFim()));
    item.setHorasAusencia(e.getHorasAusencia());
    var def = e.getDefRemId();
    item.setValorAusencia(def != null && def.getValor() != null ? def.getValor().intValue() : null);
    item.setMotivo(e.getDescricaoMotivo());
    item.setComJustificativo(e.getFlgJustificativo());
    dto.getItensFalta().add(item);

    dto.setDecisaoResponsavel(e.getDecisaoResponsavel());
    dto.setObsResponsavel(e.getObsResponsavel());
    dto.setDespachoRh(e.getDespachoRh());
    dto.setTipoJustificacao(e.getParamSitId() != null ? e.getParamSitId().getNome() : null);
    return dto;
  }

}
