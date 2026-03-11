package cv.inps.rh.avaliacao.infrastructure.mappers;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoResumoResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class AvaliacaoMapper {

    public AvaliacaoResumoResponseDTO toResumo(AvaliacaoEntity entity) {
        if (entity == null)
            return null;

        var dto = new AvaliacaoResumoResponseDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
        dto.setAno(entity.getAno());
        dto.setSemestre(entity.getSemestre());
        dto.setEstado(entity.getEstado());
        dto.setAvaliacaoFinal(entity.getAvaliacaoFinal());

        ofNullable(entity.getInstitId()).ifPresent(i -> dto.setInstitId(i.getId()));
        ofNullable(entity.getSeccaoId()).ifPresent(s -> dto.setSeccaoId(s.getId()));
        ofNullable(entity.getCargo()).ifPresent(c -> dto.setCargoId(c.getId()));
        ofNullable(entity.getCarreira()).ifPresent(c -> dto.setCarrPccsId(c.getId()));

        return dto;
    }
}
