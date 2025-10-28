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

  private Funcionario funcionario;
  private Contrato contrato;
  private ParamLocalTrab localTrab;
  private String tipoSituacao;
  private Secao secao;
  private Instituicao instituicao;
  private Estado estado;
  private String obs;

  // Construtor privado para rebuild/factory
  private Mobilidade(Long id,
                     IdentificadorUnico uuid,
                     Funcionario funcionario,
                     Contrato contrato,
                     ParamLocalTrab localTrab,
                     String tipoSituacao,
                     Secao secao,
                     Instituicao instituicao,
                     Estado estado,
                     String obs) {
    this.id = id;
    this.uuid = uuid;
    this.funcionario = funcionario;
    this.contrato = contrato;
    this.localTrab = localTrab;
    this.tipoSituacao = tipoSituacao;
    this.secao = secao;
    this.instituicao = instituicao;
    this.estado = estado;
    this.obs = obs;
  }

  // Factory para criar nova mobilidade
  public static Mobilidade create(Funcionario funcionario,
                                  Contrato contrato,
                                  ParamLocalTrab localTrab,
                                  String tipoSituacao,
                                  Secao secao,
                                  Instituicao instituicao,
                                  String obs,
                                  LocalDate dataInicio,
                                  LocalDate dataFim) {
    return new Mobilidade(
        null,
        IdentificadorUnico.create(),
        funcionario,
        contrato,
        localTrab,
        tipoSituacao,
        secao,
        instituicao,
        Estado.A,
        obs
    );
  }

  // Reconstrução a partir do repositório
  public static Mobilidade rebuild(Long id,
                                   UUID uuid,
                                   Funcionario funcionario,
                                   Contrato contrato,
                                   ParamLocalTrab localTrab,
                                   String tipoSituacao,
                                   Secao secao,
                                   Instituicao instituicao,
                                   Estado estado,
                                   String obs) {
    return new Mobilidade(
        id,
        IdentificadorUnico.from(uuid),
        funcionario,
        contrato,
        localTrab,
        tipoSituacao,
        secao,
        instituicao,
        estado,
        obs
    );
  }

  // Atualização parcial
  public void update(Funcionario funcionario,
                     Contrato contrato,
                     ParamLocalTrab localTrab,
                     String tipoSituacao,
                     Secao secao,
                     Instituicao instituicao,
                     Estado estado,
                     String obs,
                     LocalDate dataInicio,
                     LocalDate dataFim) {
    if (funcionario != null) this.funcionario = funcionario;
    if (contrato != null) this.contrato = contrato;
    if (localTrab != null) this.localTrab = localTrab;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (secao != null) this.secao = secao;
    if (instituicao != null) this.instituicao = instituicao;
    if (estado != null) this.estado = estado;
    if (obs != null) this.obs = obs;

  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }
}
