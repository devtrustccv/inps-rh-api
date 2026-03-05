package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import org.springframework.stereotype.Component;

@Component
public class NotificacaoMapper {

    public NotificacaoInfoDTO toDto(NotificacaoEntity entity) {
        if (entity == null) {
            return null;
        }

        NotificacaoInfoDTO dto = new NotificacaoInfoDTO();
        dto.setId(entity.getId());
        dto.setAssunto(entity.getAssunto());
        dto.setNomeReceptor(entity.getNomeReceptor());
        dto.setEmail(entity.getEmail());
        dto.setDataEnvio(entity.getDataEnvio() != null ? entity.getDataEnvio() : null);
        dto.setEstado(entity.getEstado());
        // Adicionar outros campos se necessário

        return dto;
    }

}
