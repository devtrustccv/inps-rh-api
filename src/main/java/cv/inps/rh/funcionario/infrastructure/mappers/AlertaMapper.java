package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.AlertaDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AlertaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlertaMapper {

    public static final String DOMINIO_TIPO = "TIPO_ALERTA_NOTIFICACAO";

    private final DomainEntityRepository domainEntityRepository;

    public AlertaDTO toDto(AlertaEntity entity) {
        return toDto(entity, domainEntityRepository.getActiveDomainByCode(DOMINIO_TIPO));
    }

    private AlertaDTO toDto(AlertaEntity entity, Map<String, String> tipos) {
        if (entity == null) {
            return null;
        }

        AlertaDTO dto = new AlertaDTO();
        dto.setUuid(entity.getUuid());
        dto.setId(entity.getId());
        dto.setReferencia(entity.getReferencia());
        dto.setReferenciaName(entity.getReferenciaName());
        dto.setReferenciaId(entity.getReferenciaId());
        dto.setReferenciaUuid(entity.getReferenciaUuid());
        if (entity.getFunId() != null) {
            dto.setFuncionarioId(entity.getFunId().getUuid());
        }
        dto.setDescricao(entity.getDescricao());
        dto.setEstado(entity.getEstado());
        dto.setTipoSituacao(entity.getTipoSituacao());
        dto.setTipoAlerta(entity.getTipoAlerta());
        dto.setTipoAlertaDescricao(tipos.getOrDefault(entity.getTipoAlerta(), entity.getTipoAlerta()));
        dto.setFlgNotificacao(entity.getFlgNotificacao());
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
        var tipos = domainEntityRepository.getActiveDomainByCode(DOMINIO_TIPO);
        return entities.stream()
                .map(e -> toDto(e, tipos))
                .collect(Collectors.toList());
    }
}
