package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class RegimeTrabalho {

  private Long id;
  private IdentificadorUnico uuid;
  private String tipoRegime;
  private String tipoSituacao;
  private LocalDate dataFim;
  private String obs;
  private Estado estado;

  private RegimeTrabalho(
      Long id,
      IdentificadorUnico uuid,
      String tipoRegime,
      String tipoSituacao,
      LocalDate dataFim,
      String obs,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.tipoRegime = tipoRegime;
    this.tipoSituacao = tipoSituacao;
    this.dataFim = dataFim;
    this.obs = obs;
    this.estado = estado;
  }

  public static RegimeTrabalho create(String tipoRegime, String tipoSituacao, LocalDate dataFim, String obs, Estado estado) {
    return new RegimeTrabalho(null, IdentificadorUnico.create(), tipoRegime, tipoSituacao, dataFim, obs, estado);
  }

  public static RegimeTrabalho rebuild(Long id, java.util.UUID uuid, String tipoRegime, String tipoSituacao, LocalDate dataFim, String obs, Estado estado) {
    return new RegimeTrabalho(id, IdentificadorUnico.from(uuid), tipoRegime, tipoSituacao, dataFim, obs, estado);
  }

  public void update(String tipoRegime, String tipoSituacao, LocalDate dataFim, String obs, Estado estado) {
    if (tipoRegime != null) this.tipoRegime = tipoRegime;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (dataFim != null) this.dataFim = dataFim;
    if (obs != null) this.obs = obs;
    if (estado != null) this.estado = estado;
  }
}
