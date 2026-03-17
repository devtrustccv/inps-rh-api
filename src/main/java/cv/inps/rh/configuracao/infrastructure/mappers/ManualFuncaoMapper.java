package cv.inps.rh.configuracao.infrastructure.mappers;

import cv.inps.rh.configuracao.application.dto.ManualFuncaoResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamManualFuncaoEntity;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class ManualFuncaoMapper {

    public ManualFuncaoResponseDTO toResponse(ParamManualFuncaoEntity entity) {
        if (entity == null)
            return null;

        var dto = new ManualFuncaoResponseDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
        dto.setDescricao(entity.getDescricao());
        dto.setEstado(entity.getEstado());

        ofNullable(entity.getInstitId()).ifPresent(i -> {
            dto.setInstitId(i.getId());
            dto.setInstituicao(i.getNome());
        });

        ofNullable(entity.getSeccaoId()).ifPresent(s -> {
            dto.setSeccaoId(s.getId());
            dto.setSeccao(s.getNome());
        });

        ofNullable(entity.getCargo()).ifPresent(c -> {
            dto.setCargoId(c.getId());
            dto.setCargo(c.getNome());
        });

        ofNullable(entity.getCarreira()).ifPresent(c -> {
            dto.setCarrPccsId(c.getId());
            dto.setCarreira(c.getNome());
        });

        return dto;
    }
}
