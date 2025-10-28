package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class RegimeTrabalho {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String tipoRegime;
  private String tipoSituacao;
  private LocalDate dataFim;
  private String obs;
  private Estado estado;

  private Contrato contrato;
  private TiposRelacionamento tiprel;

  // Construtor privado
  private RegimeTrabalho(Long id,
                         IdentificadorUnico uuid,
                         String tipoRegime,
                         String tipoSituacao,
                         LocalDate dataFim,
                         String obs,
                         Estado estado,
                         Contrato contrato,
                         TiposRelacionamento tiprel) {
    this.id = id;
    this.uuid = uuid;
    this.tipoRegime = tipoRegime;
    this.tipoSituacao = tipoSituacao;
    this.dataFim = dataFim;
    this.obs = obs;
    this.estado = estado;
    this.contrato = contrato;
    this.tiprel = tiprel;
  }

  // Factory para criar novo regime
  public static RegimeTrabalho create(String tipoRegime,
                                      String tipoSituacao,
                                      LocalDate dataFim,
                                      String obs,
                                      Funcionario funcionario,
                                      Contrato contrato,
                                      TiposRelacionamento tiprel) {
    return new RegimeTrabalho(
        null,
        IdentificadorUnico.create(),
        tipoRegime,
        tipoSituacao,
        dataFim,
        obs,
        Estado.A,
        contrato,
        tiprel
    );
  }

  // Rebuild a partir da Entity
  public static RegimeTrabalho rebuild(Long id,
                                       UUID uuid,
                                       String tipoRegime,
                                       String tipoSituacao,
                                       LocalDate dataFim,
                                       String obs,
                                       Estado estado,
                                       Contrato contrato,
                                       TiposRelacionamento tiprel) {
    return new RegimeTrabalho(
        id,
        IdentificadorUnico.from(uuid),
        tipoRegime,
        tipoSituacao,
        dataFim,
        obs,
        estado,
        contrato,
        tiprel
    );
  }

  // Atualização parcial
  public void update(String tipoRegime,
                     String tipoSituacao,
                     LocalDate dataFim,
                     String obs,
                     Estado estado,
                     Funcionario funcionario,
                     Contrato contrato,
                     TiposRelacionamento tiprel) {
    if (tipoRegime != null) this.tipoRegime = tipoRegime;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (dataFim != null) this.dataFim = dataFim;
    if (obs != null) this.obs = obs;
    if (estado != null) this.estado = estado;
    if (contrato != null) this.contrato = contrato;
    if (tiprel != null) this.tiprel = tiprel;
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }
}
