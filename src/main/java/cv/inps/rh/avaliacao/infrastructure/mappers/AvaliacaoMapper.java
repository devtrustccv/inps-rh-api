package cv.inps.rh.avaliacao.infrastructure.mappers;

import cv.inps.rh.avaliacao.application.dto.ObjetivoAvaliacaoResumoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class AvaliacaoMapper {

    public ObjetivoAvaliacaoResumoDTO toResumo(AvaliacaoEntity entity) {
        if (entity == null)
            return null;

        var dto = new ObjetivoAvaliacaoResumoDTO();
        dto.setId(entity.getId());
        dto.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
        dto.setAno(entity.getAno());
        dto.setSemestre(entity.getSemestre());
        dto.setEstado(entity.getEstado());

        ofNullable(entity.getInstitId()).ifPresent(i -> {
            dto.setInstitId(i.getId());
            dto.setInstitNome(i.getNome());
        });
        ofNullable(entity.getSeccaoId()).ifPresent(s -> {
            dto.setSeccaoId(s.getId());
            dto.setSeccaoNome(s.getNome());
        });
        ofNullable(entity.getCargo()).ifPresent(c -> {
            dto.setCargoId(c.getId());
            dto.setCargoNome(c.getNome());
        });
        ofNullable(entity.getCarreira()).ifPresent(c -> {
            dto.setCarrPccsId(c.getId());
            dto.setCarrPccsNome(c.getNome());
        });

        return dto;
    }
}
