package cv.inps.rh.assiduidade.infrastructure.persistence.projections;

import java.time.LocalDate;
import java.util.UUID;

public interface AssiduidadeResumoViewRow {
    UUID getUuidFuncionairio();

    String getNomeColaborador();

    String getDirecao();

    Integer getMes();

    Integer getAno();

    Long getTotalDias();

    Long getTotalFalta();

    Long getTotalHorasTrabalhadas();

    Long getTotalHorasAusentes();

    Long getTotalHoraExtra();

    Long getTotalHoraAlmoco();

    String getEstado();

    LocalDate getData();
}
