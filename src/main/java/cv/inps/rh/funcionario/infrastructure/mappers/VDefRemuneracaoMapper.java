package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.RenumeracaoListDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.VDefRemuneracaoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import org.springframework.stereotype.Component;

@Component
public class VDefRemuneracaoMapper {

  public RenumeracaoListDTO toDTO(VDefRemuneracaoEntity entity) {
    if (entity == null) return null;

    RenumeracaoListDTO dto = new RenumeracaoListDTO();
    dto.setId(entity.getRemId());
    dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    dto.setEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
    dto.setEstadoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);
    dto.setMovimento(entity.getDescricao());
    dto.setValor(entity.getValor() != null ? entity.getValor().toPlainString() : null);
    dto.setPercentagem(entity.getPercentagem() != null ? entity.getPercentagem().toPlainString() : null);
    dto.setObs(entity.getObs());
    dto.setUltimoPRoc(entity.getDataUltimoProc() != null ? DateFormatter.localDateToString(entity.getDataUltimoProc()) : null);
    dto.setDataInicio(entity.getDataInicio() != null ? DateFormatter.localDateToString(entity.getDataInicio()) : null);
    dto.setDataFim(entity.getDataFim() != null ? DateFormatter.localDateToString(entity.getDataFim()) : null);
    dto.setContrVinculoId(entity.getContrVinculoId());
    dto.setCarreiraId(entity.getCarreiraId());
    dto.setSituacLaboralId(entity.getSituacLaboralId());
    dto.setUserRegistoName(entity.getUserRegistoName());
    dto.setUserAlteracaoName(entity.getUserAlteracaoName());

    return dto;
  }
}