package cv.inps.rh.funcionario.domain.projections;

import java.time.LocalDate;

public interface HistoricoLaboralViewRow {
    String getFuncionarioUuid();

    Long getTiprelId();

    String getTipoContratoDesc();

    String getVinculoDesc();

    String getDirecaoDesc();

    String getSeccaoDesc();

    String getCarreiraDesc();

    String getReferenciaEscalaoDesc();

    String getCargoDesc();

    String getSituacaoLaboralDesc();

    LocalDate getDataInicio();

    LocalDate getDataFim();

    String getTipoSituacaoDesc();

    Integer getUltimoVinculo();
}
