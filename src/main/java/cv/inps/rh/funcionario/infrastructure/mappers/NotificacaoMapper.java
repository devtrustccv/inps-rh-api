package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.shared.application.dto.NotificacaoInfoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificacaoMapper {

    public NotificacaoInfoDTO toDto(NotificacaoEntity entity) {
        if (entity == null) {
            return null;
        }

        NotificacaoInfoDTO dto = new NotificacaoInfoDTO();
        dto.setId(entity.getId());
        dto.setTipoNotificacao(entity.getTipoNotificacao());
        // Sem template configurado o registo fica sem assunto: mostra-se o tipo, para a linha não
        // aparecer vazia na lista.
        dto.setAssunto(entity.getAssunto() != null && !entity.getAssunto().isBlank()
            ? entity.getAssunto() : assuntoPorTipo(entity.getTipoNotificacao()));
        dto.setCorpo(entity.getMessage());
        dto.setNomeReceptor(entity.getNomeReceptor());
        if (entity.getFunId() != null) {
            dto.setFuncionarioId(entity.getFunId().getUuid());
        }
        dto.setEmail(entity.getEmail());
        dto.setDataEnvio(entity.getDataEnvio() != null ? entity.getDataEnvio() : null);
        dto.setEstado(entity.getEstado());
        // Adicionar outros campos se necessário

        return dto;
    }

    /** "VALIDACAO_REGISTO_COLABORADOR" → "Validacao registo colaborador". */
    static String assuntoPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) return null;
        var t = tipo.trim().replace('_', ' ').toLowerCase();
        return Character.toUpperCase(t.charAt(0)) + t.substring(1);
    }

    public List<NotificacaoInfoDTO> toDtoList(List<NotificacaoEntity> entities) {
      return entities.stream().map(this::toDto).toList();
    }

}
