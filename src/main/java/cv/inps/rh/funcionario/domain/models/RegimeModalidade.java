package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.Optional;
import java.util.UUID;

@Getter
public class RegimeModalidade {

  private Long id;
  private String modalidade;
  private String diasSemana;
  private Integer numHoras;
  private IdentificadorUnico uuid;
  private Estado estado;


  private RegimeModalidade(Long id, String modalidade, String diasSemana, Integer numHoras, IdentificadorUnico uuid, Estado estado) {
    this.id = id;
    this.modalidade = modalidade;
    this.diasSemana = diasSemana;
    this.numHoras = numHoras;
    this.uuid = uuid;
    this.estado = estado;
  }

  public static RegimeModalidade create(Long id ,String modalidade, String diasSemana, Integer numHoras) {
    return new RegimeModalidade(
        id!=null && id > 0 ? id : null, modalidade, diasSemana, numHoras, IdentificadorUnico.create(), Estado.A
    );
  }

  public static RegimeModalidade rebuild(Long id, String modalidade, String diasSemana, Integer numHoras, UUID uuid, Estado estado) {
    return new RegimeModalidade(
        id, modalidade, diasSemana, numHoras, IdentificadorUnico.from(uuid), estado
    );
  }



  public void update(String modalidade, String diasSemana, Integer numHoras) {
    if (modalidade != null && !modalidade.isBlank()) this.modalidade = modalidade;
    if (diasSemana != null && !diasSemana.isBlank()) this.diasSemana = diasSemana;
    if (numHoras != null) this.numHoras = numHoras;
  }

  public void eliminar() {
    this.estado = Estado.E;
  }


}
