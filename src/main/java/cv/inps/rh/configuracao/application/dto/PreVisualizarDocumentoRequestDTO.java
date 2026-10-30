package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Pré-visualização de um Documento de Saída. Sem {@code corpo}, usa o documento ativo de {@code tipoDocumento};
 * com {@code corpo} (e opcionalmente {@code titulo}/{@code assinadoPor}), usa esse texto — para testar antes de gravar.
 */
@Data
@NoArgsConstructor
@IgrpDTO
public class PreVisualizarDocumentoRequestDTO {
  private String tipoDocumento;
  private String titulo;
  private String corpo;
  private String assinadoPor;
  /** nome do marcador → valor. */
  private Map<String, Object> valores;
}
