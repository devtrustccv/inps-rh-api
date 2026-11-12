package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Resultado da pré-visualização de um Documento de Saída (marcadores já substituídos). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class DocumentoRenderizadoDTO {
  private String tipoDocumento;
  private String titulo;
  /** Valores tal como foram passados (templates com th:text). */
  private String corpo;
  /** Valores escapados para HTML (templates com th:utext, ex.: Ordem de Serviço). */
  private String corpoHtml;
  private String assinadoPor;
  private String responsavel;
  /** Marcadores do texto sem valor nem valor por defeito (saíram como "—"). */
  private List<String> marcadoresEmFalta;
}
