package cv.inps.rh.funcionario.domain.projections;

public interface MobilidadeList {

  Long getId();

  Long getIdFuncionario();

  String getUuid();

  String getUuidFuncionario();

  String getCargo();

  String getDireccao();

  String getSeccao();

  String getLocalTrabalho();

  String getDataInicio();

  String getDataFim();

  String getProcessamento();

  String getEstado();

  long getTotalCount();
}
