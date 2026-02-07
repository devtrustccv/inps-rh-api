package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.ValidacaoResponseDTO;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidacaoMapper {


  public ValidacaoResponseDTO toDto(ValidacaoEntity validacao) {
    if (validacao == null) return null;

    var dto = new ValidacaoResponseDTO();
    dto.setId(validacao.getId());
    dto.setUuid(validacao.getUuid() != null ? validacao.getUuid().toString() : null);
    dto.setNomeColaborador(validacao.getFunId() != null ? validacao.getFunId().getNome() : "");
    dto.setTipoOperacao(validacao.getTipoAccao());
    dto.setReferenciaName(validacao.getReferenciaName());
    dto.setDataOperacao(DateFormatter.localDateTimeToString((validacao.getCreatedDate())));
    dto.setUtilizador(validacao.getCreatedBy());
    dto.setReferenciaUuid(validacao.getReferenciaUuid()!=null ? validacao.getReferenciaUuid().toString() : null);
    dto.setReferenciaId(validacao.getReferenciaId());
    dto.setIdFuncionario(validacao.getFunId() != null ? validacao.getFunId().getId() : 0L);
    dto.setUuidFuncionario(validacao.getFunId() != null ? validacao.getFunId().getUuid().toString(): null);

    return dto;
  }


}
