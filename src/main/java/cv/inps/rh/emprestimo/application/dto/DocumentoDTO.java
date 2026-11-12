/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DocumentoDTO {

  private String id;

  // Tipo e ficheiro validados em EmprestimoDocumentService (ignora as linhas vazias dos
  // formulários e dá mensagens que o utilizador entende).
  private Long tipoDocumentoId;

  private String tipoDocumentoDesc;

  private String referenciaTipoDocumento;

  private String url;
}
