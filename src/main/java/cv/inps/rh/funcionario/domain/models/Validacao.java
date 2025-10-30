package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Validacao {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String tipoAccao;
  private String referenciaName;
  private Long referenciaId;
  private Estado estado;
  private String obs;
  private TiposRelacionamento tiprel;
  private LocalDate dataRegistro;
  private String userRegistro;

  private Validacao(Long id,
                    IdentificadorUnico uuid,
                    String tipoAccao,
                    String referenciaName,
                    Long referenciaId,
                    Estado estado,
                    String obs,
                    TiposRelacionamento tiprel,
                    LocalDate dataRegistro,
                    String userRegistro) {
    this.id = id;
    this.uuid = uuid;
    this.tipoAccao = tipoAccao;
    this.referenciaName = referenciaName;
    this.referenciaId = referenciaId;
    this.estado = estado;
    this.obs = obs;
    this.tiprel = tiprel;
    this.dataRegistro = dataRegistro;
    this.userRegistro = userRegistro;
  }

  public static Validacao create(String tipoAccao,
                                 String referenciaName,
                                 Long referenciaId,
                                 String obs,
                                 TiposRelacionamento tiprel) {
    return new Validacao(
        null,
        IdentificadorUnico.create(),
        tipoAccao,
        referenciaName,
        referenciaId,
        Estado.P,
        obs,
        tiprel,
        null,
        null
    );
  }

  public static Validacao rebuild(Long id,
                                  UUID uuid,
                                  String tipoAccao,
                                  String referenciaName,
                                  Long referenciaId,
                                  Estado estado,
                                  String obs,
                                  TiposRelacionamento tiprel,
                                  LocalDate dataRegistro,
                                  String userRegistro) {
    return new Validacao(
        id,
        IdentificadorUnico.from(uuid),
        tipoAccao,
        referenciaName,
        referenciaId,
        estado,
        obs,
        tiprel ,
        dataRegistro,
        userRegistro
    );
  }
}
