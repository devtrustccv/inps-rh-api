package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.ParamSitLaboral;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class SituacaoLaboral {

  private Long id;
  private ParamSitLaboral paramSitLaboral;
  private String motivoSitLab;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Estado estado;
  private String obs;
  private IdentificadorUnico uuid;

  private SituacaoLaboral(
      Long id,
      ParamSitLaboral paramSitLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado,
      String obs,
      IdentificadorUnico uuid
  ) {
    this.id = id;
    this.paramSitLaboral = paramSitLaboral;
    this.motivoSitLab = motivoSitLab;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.estado = estado;
    this.obs = obs;
    this.uuid = uuid;
  }

  public static SituacaoLaboral create(
      ParamSitLaboral paramSitLaboral,
      String motivoSitLab,
      String obs,
      LocalDate dataInicio,
      LocalDate dataFim
  ) {
    if (paramSitLaboral == null) {
      throw IgrpResponseStatusException.badRequest("paramSitLaboral é Obrigatorio");
    }
    return new SituacaoLaboral(
        null, paramSitLaboral, motivoSitLab, dataInicio, dataFim, Estado.P, obs, IdentificadorUnico.create()
    );
  }

  public static SituacaoLaboral rebuild(
      Long id,
      ParamSitLaboral paramSitLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado,
      String obs,
      UUID uuid
  ) {
    return new SituacaoLaboral(
        id, paramSitLaboral, motivoSitLab, dataInicio, dataFim, estado, obs, IdentificadorUnico.from(uuid)
    );
  }

  public void update(
      ParamSitLaboral paramSitLaboral,
      String motivoSitLab,
      LocalDate dataInicio,
      LocalDate dataFim,
      Estado estado,
      String obs
  ) {
    if (paramSitLaboral != null) {
      this.paramSitLaboral = paramSitLaboral;
    }
    this.motivoSitLab = motivoSitLab;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.estado = estado;
    this.obs = obs;
  }


}
