package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AlertaDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertaMapper {

    public AlertaDTO toDto(AlertaEntity entity) {
        if (entity == null) {
            return null;
        }

        AlertaDTO dto = new AlertaDTO();
        dto.setUuid(entity.getUuid());
        dto.setId(entity.getId());
        dto.setReferenciaName(entity.getReferenciaName());
        dto.setReferenciaId(entity.getReferenciaId());
        dto.setDescricao(entity.getDescricao());
        dto.setEstado(entity.getEstado());
        dto.setTipoSituacao(entity.getTipoSituacao());
        dto.setTipoAlerta(entity.getTipoAlerta());
        dto.setPrioridade(entity.getPrioridade());

        if (entity.getCreatedDate() != null) {
            dto.setDataRegisto(entity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return dto;
    }

    public List<AlertaDTO> toDtoList(List<AlertaEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
