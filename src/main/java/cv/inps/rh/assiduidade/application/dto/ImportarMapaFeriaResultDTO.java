package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado da importação do mapa de férias. A importação é tolerante por linha: as linhas válidas
 * são gravadas e as inválidas ficam descritas em {@link #erros} para o RH corrigir e reimportar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ImportarMapaFeriaResultDTO {

  @Schema(description = "Ano de referência do mapa importado")
  private Integer ano;

  @Schema(description = "Linhas com conteúdo lidas do ficheiro (exclui cabeçalho e linhas vazias)")
  private Integer linhasLidas;

  @Schema(description = "Colaboradores cujo mapa foi importado com sucesso")
  private Integer colaboradoresImportados;

  @Schema(description = "Total de períodos de férias gravados (cada colaborador pode ter dois)")
  private Integer periodosImportados;

  @Schema(description = "Todos os erros encontrados no ficheiro, separados por \"; \". "
      + "Vazio quando o ficheiro foi todo importado.",
      example = "Linha 4: Colaborador 99999999 não encontrado; Linha 5: Férias 1: é preciso "
          + "preencher início e fim")
  private String erros;
}
