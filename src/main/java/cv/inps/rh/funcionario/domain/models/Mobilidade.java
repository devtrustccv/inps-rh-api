package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.ParamLocalTrab;
import cv.inps.rh.parametrizacao.domain.models.Secao;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.Instituicao;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Mobilidade {

  private final Long id;
  private final IdentificadorUnico uuid;

  private ParamLocalTrab localTrab;
  private String tipoSituacao;
  private Secao secao;
  private Instituicao instituicao;
  private Estado estado;
  private String obs;
  private LocalDate dataInicio;
  private LocalDate dataFim;

  // Construtor privado para rebuild/factory
  private Mobilidade(Long id,
                     IdentificadorUnico uuid,
                     ParamLocalTrab localTrab,
                     String tipoSituacao,
                     Secao secao,
                     Instituicao instituicao,
                     Estado estado,
                     String obs,
                     LocalDate dataInicio,
                     LocalDate dataFim) {
    this.id = id;
    this.uuid = uuid;
    this.localTrab = localTrab;
    this.tipoSituacao = tipoSituacao;
    this.secao = secao;
    this.instituicao = instituicao;
    this.estado = estado;
    this.obs = obs;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
  }

  // Factory para criar nova mobilidade
  public static Mobilidade create(ParamLocalTrab localTrab,
                                  String tipoSituacao,
                                  Secao secao,
                                  Instituicao instituicao,
                                  String obs,
                                  LocalDate dataInicio,
                                  LocalDate dataFim) {
    return new Mobilidade(
        null,
        IdentificadorUnico.create(),
        localTrab,
        tipoSituacao,
        secao,
        instituicao,
        Estado.P,
        obs,
        dataInicio,
        dataFim
    );
  }

  // Reconstrução a partir do repositório
  public static Mobilidade rebuild(Long id,
                                   UUID uuid,
                                   ParamLocalTrab localTrab,
                                   String tipoSituacao,
                                   Secao secao,
                                   Instituicao instituicao,
                                   Estado estado,
                                   String obs,
                                   LocalDate dataInicio,
                                   LocalDate dataFim) {
    return new Mobilidade(
        id,
        IdentificadorUnico.from(uuid),
        localTrab,
        tipoSituacao,
        secao,
        instituicao,
        estado,
        obs,
        dataInicio,
        dataFim
    );
  }


  public void update(ParamLocalTrab localTrab,
                     String tipoSituacao,
                     Secao secao,
                     Instituicao instituicao,
                     String obs,
                     LocalDate dataInicio,
                     LocalDate dataFim) {
    if (localTrab != null) this.localTrab = localTrab;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (secao != null) this.secao = secao;
    if (instituicao != null) this.instituicao = instituicao;
    if (obs != null) this.obs = obs;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;

  }


  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  public void mudarEstado(Estado estado) {
    this.estado = estado;
  }

    public void fechar(LocalDate dataFimContrato) {
      this.dataFim = dataFimContrato;
    }
}
