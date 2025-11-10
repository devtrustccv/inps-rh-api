package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DocumentoPessoal {

  private Long id;
  private String numDocumento;
  private TipoDocumento tipoDocumento;
  private Estado estado;
  private IdentificadorUnico uuid;

  private DocumentoPessoal(Long id, String numDocumento, TipoDocumento tipoDocumento, Estado estado, IdentificadorUnico uuid) {
    this.id = id;
    this.numDocumento = numDocumento;
    this.tipoDocumento = tipoDocumento;
    this.estado = estado;
    this.uuid = uuid;
  }

  public static DocumentoPessoal create(String numDocumento, TipoDocumento tipoDocumento) {
    return new DocumentoPessoal(
        null,
        numDocumento,
        tipoDocumento,
        Estado.P,
        IdentificadorUnico.create()
    );
  }

  public static DocumentoPessoal rebuild(Long id, String numDocumento, TipoDocumento tipoDocumento,
                                         Estado estado, UUID uuid) {
    return new DocumentoPessoal(id, numDocumento, tipoDocumento, estado, IdentificadorUnico.from(uuid));
  }


  public void update(String numDocumento, TipoDocumento tipoDocumento) {
    this.numDocumento = numDocumento;
    this.tipoDocumento = tipoDocumento;
  }

  public void eliminar() {
    this.estado = Estado.E;
  }

}
