package cv.inps.rh.avaliacao.infrastructure.mappers;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoListagemResponseDTO;
import cv.inps.rh.avaliacao.application.dto.PeriodoResumoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Optional.ofNullable;

@Component
public class AvaliacaoListagemMapper {

    /**
     * Linha-pai da grelha. Os períodos vêm já construídos porque saem de
     * RH_T_AVD_DETALHE e não da entidade da avaliação.
     */
    public AvaliacaoListagemResponseDTO toListagem(
            AvaliacaoEntity base,
            String estado,
            List<PeriodoResumoDTO> periodos,
            BigDecimal notaFinal,
            String notaFinalQualitativa) {

        if (base == null)
                return null;

        var dto = new AvaliacaoListagemResponseDTO();

        dto.setUuid(base.getUuid() != null ? base.getUuid().toString() : null);
        dto.setAno(base.getAno());
        dto.setAbrangencia(base.getAbrangencia());

        ofNullable(base.getFuncionario()).ifPresent(f -> {
            dto.setFunId(f.getId());
            dto.setFunUuid(f.getUuid());
            dto.setNomeColaborador(f.getNome());
        });

        ofNullable(base.getInstitId()).ifPresent(i -> {
            dto.setInstitId(i.getId());
            dto.setNomeInstituicao(i.getNome());
        });

        ofNullable(base.getCargo()).ifPresent(c -> {
            dto.setCargoId(c.getId());
            dto.setCargoNome(c.getNome());
        });

        ofNullable(base.getSeccaoId()).ifPresent(s -> {
            dto.setSeccaoId(s.getId());
            dto.setSeccaoNome(s.getNome());
        });

        ofNullable(base.getCarreira()).ifPresent(c -> {
            dto.setCarrPccsId(c.getId());
            dto.setCarrPccsNome(c.getNome());
        });

        dto.setEstado(estado);
        dto.setEstadoDescricao(cv.inps.rh.shared.application.constants.EstadoAvaliacao
                .resolver(estado, periodos != null && !periodos.isEmpty()).getDescricao());
        dto.setPeriodos(periodos != null ? periodos : List.of());
        dto.setNotaFinal(notaFinal);
        dto.setNotaFinalQualitativa(notaFinalQualitativa);

        return dto;
    }
}
