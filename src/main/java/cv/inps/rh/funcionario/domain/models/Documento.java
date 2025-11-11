package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

@Getter
public class Documento {

  private final Long id;
  private final IdentificadorUnico uuid;
  private TipoDocumento tipoDocumento;
  private Long docId;
  private String referenciaName;
  private String referenciaId;
  private Estado estado;

  private Documento(
      Long id,
      IdentificadorUnico uuid,
      TipoDocumento tipoDocumento,
      Long docId,
      String referenciaName,
      String referenciaId,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.tipoDocumento = tipoDocumento;
    this.docId = docId;
    this.referenciaName = referenciaName;
    this.referenciaId = referenciaId;
    this.estado = estado;
  }

  // Factory para criar novo documento
  public static Documento create(
      Long id,
      TipoDocumento tipoDocumento,
      Long docId,
      String referenciaName,
      String referenciaId
  ) {
    return new Documento(
        id!=null && id>0 ? id : null,
        IdentificadorUnico.create(),
        tipoDocumento,
        docId,
        referenciaName,
        referenciaId,
        Estado.P
    );
  }

  // Reconstrução para repositório
  public static Documento rebuild(
      Long id,
      java.util.UUID uuid,
      TipoDocumento tipoDocumento,
      Long docId,
      String referenciaName,
      String referenciaId,
      Estado estado
  ) {
    return new Documento(
        id,
        IdentificadorUnico.from(uuid),
        tipoDocumento,
        docId,
        referenciaName,
        referenciaId,
        estado
    );
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  // Update parcial
  public void update(
      TipoDocumento tipoDocumento,
      Long docId,
      String referenciaName,
      String referenciaId
  ) {
    if (tipoDocumento != null) this.tipoDocumento = tipoDocumento;
    if (docId != null) this.docId = docId;
    if (referenciaName != null) this.referenciaName = referenciaName;
    if (referenciaId != null) this.referenciaId = referenciaId;
  }

}
