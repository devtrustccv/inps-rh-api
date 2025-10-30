package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class SituacaoLaboral {

  private Long id;
  private String situacaoLaboral;
  private String motivoSitLab;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Contrato contrato;
  private Estado estado;
  private String obs;
  private IdentificadorUnico uuid;

  private SituacaoLaboral(
      Long id,
      String situacaoLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Contrato contrato,
      Estado estado,
      String obs,
      IdentificadorUnico uuid
  ) {
    this.id = id;
    this.situacaoLaboral = situacaoLaboral;
    this.motivoSitLab = motivoSitLab;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.contrato = contrato;
    this.estado = estado;
    this.obs = obs;
    this.uuid = uuid;
  }

  public static SituacaoLaboral create(
      String situacaoLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Contrato contrato,
      String obs
  ) {
    if (situacaoLaboral == null || situacaoLaboral.isBlank()) {
      throw new IllegalArgumentException("situacaoLaboral is mandatory");
    }
    return new SituacaoLaboral(
        null, situacaoLaboral, motivoSitLab, dataInicio, dataFim, contrato, Estado.P, obs, IdentificadorUnico.create()
    );
  }

  public static SituacaoLaboral rebuild(
      Long id,
      String situacaoLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Contrato contrato,
      Estado estado,
      String obs,
      UUID uuid
  ) {
    return new SituacaoLaboral(
        id, situacaoLaboral, motivoSitLab, dataInicio, dataFim, contrato, estado, obs, IdentificadorUnico.from(uuid)
    );
  }

  public void update(
      String situacaoLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Contrato contrato,
      Estado estado,
      String obs
  ) {
    if (situacaoLaboral != null && !situacaoLaboral.isBlank()) {
      this.situacaoLaboral = situacaoLaboral;
    }
    this.motivoSitLab = motivoSitLab;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.contrato = contrato;
    this.estado = estado;
    this.obs = obs;
  }


}
