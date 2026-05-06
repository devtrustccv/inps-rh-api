package cv.inps.rh.avaliacao.infrastructure.mappers;

import cv.inps.rh.avaliacao.application.dto.AvaliacaoListagemResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static java.util.Optional.ofNullable;

@Component
public class AvaliacaoListagemMapper {

    public AvaliacaoListagemResponseDTO toListagem(
            AvaliacaoEntity base,
            String estado,
            BigDecimal avaliacaoFinalSemestre1,
            BigDecimal avaliacaoFinalSemestre2,
            BigDecimal notaFinal,
            String notaFinalQualitativa) {

        if (base == null)
                return null;

        var dto = new AvaliacaoListagemResponseDTO();

        dto.setUuid(base.getUuid() != null ? base.getUuid().toString() : null);

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
        dto.setAvaliacaoFinalSemestre1(avaliacaoFinalSemestre1);
        dto.setAvaliacaoFinalSemestre2(avaliacaoFinalSemestre2);
        dto.setNotaFinal(notaFinal);
        dto.setNotaFinalQualitativa(notaFinalQualitativa);
        dto.setSemestreNota(buildSemestreNota(avaliacaoFinalSemestre1, avaliacaoFinalSemestre2));

        return dto;
    }

    private String buildSemestreNota(BigDecimal s1, BigDecimal s2) {
        var p1 = s1 != null ? s1.toString() : "-";
        var p2 = s2 != null ? s2.toString() : "-";
        return "1º Sem: " + p1 + " / 2º Sem: " + p2;
    }
}
