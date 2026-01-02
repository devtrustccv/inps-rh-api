package cv.inps.rh.funcionario.domain.projections;

public interface RelacaoLaboralView {
  String getFuncionarioUuid();

  String getContratoDesc();

  Long getContratoId();

  String getVinculoDesc();

  Long getVinculoId();

  String getDirecaoDesc();

  Long getDirecaoId();

  String getSeccaoDesc();

  Long getSeccaoId();

  String getCarreiraDesc();

  Long getCarreiraId();

  String getEscalaoDesc();

  Long getEscalaoId();

  String getDataCarreira();

  String getDataContrato();

  String getCargoDesc();

  Long getCargoId();

  String getSituacaoLaboralDesc();

  Long getSituacaoLaboralId();
}
