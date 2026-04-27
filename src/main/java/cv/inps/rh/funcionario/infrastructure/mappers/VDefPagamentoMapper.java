package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.PagamentosDescontoListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.VDefPagamentoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import org.springframework.stereotype.Component;

@Component
public class VDefPagamentoMapper {

  public PagamentosDescontoListDTO toDTO(VDefPagamentoEntity entity) {
    if (entity == null) return null;

    PagamentosDescontoListDTO dto = new PagamentosDescontoListDTO();
    dto.setId(entity.getPagId() != null ? entity.getPagId().toString() : null);
    dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    dto.setMovimento(entity.getDescricao());
    dto.setEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
    dto.setEstadoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);
    dto.setValor(entity.getValor() != null ? entity.getValor().toPlainString() : null);
    dto.setPercentagem(entity.getPercentagem() != null ? entity.getPercentagem().toPlainString() : null);
    dto.setObs(entity.getObs());
    dto.setDataInicio(entity.getDataInicio() != null ? DateFormatter.localDateToString(entity.getDataInicio()) : null);
    dto.setDataFim(entity.getDataFim() != null ? DateFormatter.localDateToString(entity.getDataFim()) : null);
    dto.setUltimoProc(entity.getDataUltimoProc() != null ? DateFormatter.localDateToString(entity.getDataUltimoProc()) : null);
    dto.setContrVinculoId(entity.getContrVinculoId());
    dto.setCarreiraId(entity.getCarreiraId());
    dto.setSituacLaboralId(entity.getSituacLaboralId());
    dto.setUserRegistoName(entity.getUserRegistoName());
    dto.setUserAlteracaoName(entity.getUserAlteracaoName());

    return dto;
  }
}