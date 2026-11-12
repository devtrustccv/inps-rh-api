package cv.inps.rh.shared.domain.service.documento;

import lombok.Value;

import java.util.Set;

/**
 * Documento de Saída (RH_T_PARAM_DOC_OUTPUT) com os marcadores {@code ${nome}} já substituídos.
 * Tem getters para poder ser usado diretamente num template Thymeleaf ({@code ${param.corpo}}).
 */
@Value
public class DocumentoRenderizado {

  String tipoDocumento;
  String titulo;
  /** Corpo com os valores tal como foram passados — para templates que imprimem com {@code th:text}. */
  String corpo;
  /** Corpo com os valores escapados para HTML — para templates que imprimem com {@code th:utext}. */
  String corpoHtml;
  String assinadoPor;
  /** Nome do responsável configurado no documento (null se não tiver). */
  String responsavel;
  /** Marcadores do texto que não tinham valor nem valor por defeito (saíram como "—"). */
  Set<String> marcadoresEmFalta;
}
