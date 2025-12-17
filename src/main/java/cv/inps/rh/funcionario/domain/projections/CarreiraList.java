package cv.inps.rh.funcionario.domain.projections;

import java.time.LocalDate;

public interface CarreiraList {

  Long getId();
  String getUuid();
  Long getIdFuncionario();
  String getUuidFuncionario();
  String getTipoCarreira();
  String getVinculo();
  String getCarreira();
  String getCargo();
  String getEscalao();
  String getSalario();
  String getSituacaoLaboral();
  LocalDate getDataInicio();
  LocalDate getDataFim();
  String getProcessamento();
  String getEstado();
  Long getTotalCount();
}
