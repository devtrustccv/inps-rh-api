package cv.inps.rh.shared.infrastructure.persistence.projections;

import java.util.UUID;

/** Uma linha da Lista de Férias: o direito de férias de um funcionário num ano (RH_V_FERIAS_MENSAL agrupada). */
public interface FeriasResumoAnoProjection {
  UUID getUuidFuncionario();
  Integer getAno();
  String getNomeColaborador();
  String getDirecao();
  String getSecao();
  String getVinculo();
  String getCategoria();
  Number getTotalDireito();
  Number getTotalDireitoAno();
  Number getTotalPlaneado();
  Number getTotalGozado();
}
