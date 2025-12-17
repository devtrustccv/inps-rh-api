package cv.inps.rh.funcionario.domain.projections;

import java.time.LocalDate;

public interface FuncionarioList {
  Long getId();
  String  getUuid();
  String getNome();
  String getCargo();
  LocalDate getDataInicio();
  String getDireccao();
  String getSeccao();
  String getCarreiraCategoria();
  String getEstadoRegisto();
  String getEstadoColaborador();
  Long getTotalCount();
}
