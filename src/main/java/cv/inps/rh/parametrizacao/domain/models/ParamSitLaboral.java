package cv.inps.rh.parametrizacao.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class ParamSitLaboral {

  private Long id;
  private IdentificadorUnico uuid;
  private String codigo;
  private String nome;
  private String tipoSituacao;
  private Integer flgRenumeracao;
  private Integer flgAfetaCarreira;
  private Integer flgContaTempServico;
  private Integer flgCessaProgressao;
  private Integer flgEstadoContrato;
  private Estado estado;

  private ParamSitLaboral(
      Long id,
      IdentificadorUnico uuid,
      String codigo,
      String nome,
      String tipoSituacao,
      Integer flgRenumeracao,
      Integer flgAfetaCarreira,
      Integer flgContaTempServico,
      Integer flgCessaProgressao,
      Integer flgEstadoContrato,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.codigo = codigo;
    this.nome = nome;
    this.tipoSituacao = tipoSituacao;
    this.flgRenumeracao = flgRenumeracao;
    this.flgAfetaCarreira = flgAfetaCarreira;
    this.flgContaTempServico = flgContaTempServico;
    this.flgCessaProgressao = flgCessaProgressao;
    this.flgEstadoContrato = flgEstadoContrato;
    this.estado = estado;
  }

  public static ParamSitLaboral create(
      String codigo,
      String nome,
      String tipoSituacao,
      Integer flgRenumeracao,
      Integer flgAfetaCarreira,
      Integer flgContaTempServico,
      Integer flgCessaProgressao,
      Integer flgEstadoContrato,
      Estado estado
  ) {
    return new ParamSitLaboral(
        null,
        IdentificadorUnico.create(),
        codigo,
        nome,
        tipoSituacao,
        flgRenumeracao,
        flgAfetaCarreira,
        flgContaTempServico,
        flgCessaProgressao,
        flgEstadoContrato,
        estado
    );
  }

  public static ParamSitLaboral rebuild(
      Long id,
      java.util.UUID uuid,
      String codigo,
      String nome,
      String tipoSituacao,
      Integer flgRenumeracao,
      Integer flgAfetaCarreira,
      Integer flgContaTempServico,
      Integer flgCessaProgressao,
      Integer flgEstadoContrato,
      Estado estado
  ) {
    return new ParamSitLaboral(
        id,
        IdentificadorUnico.from(uuid),
        codigo,
        nome,
        tipoSituacao,
        flgRenumeracao,
        flgAfetaCarreira,
        flgContaTempServico,
        flgCessaProgressao,
        flgEstadoContrato,
        estado
    );
  }

  public void update(
      String nome,
      String tipoSituacao,
      Integer flgRenumeracao,
      Integer flgAfetaCarreira,
      Integer flgContaTempServico,
      Integer flgCessaProgressao,
      Integer flgEstadoContrato
  ) {
    if (nome != null) this.nome = nome;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (flgRenumeracao != null) this.flgRenumeracao = flgRenumeracao;
    if (flgAfetaCarreira != null) this.flgAfetaCarreira = flgAfetaCarreira;
    if (flgContaTempServico != null) this.flgContaTempServico = flgContaTempServico;
    if (flgCessaProgressao != null) this.flgCessaProgressao = flgCessaProgressao;
    if (flgEstadoContrato != null) this.flgEstadoContrato = flgEstadoContrato;
  }
}
