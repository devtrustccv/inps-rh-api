/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoPrestadorRequestDTO {

  // Valor do domínio AVALIACAO_FORNECEDOR (referência AVALIACAO): "100" | "75" | "50" | "25" — todos obrigatórios
  private String sistemaQualidade;
  private String prazoFornecimento;
  private String qualidadeProduto;
  private String capacidadeResposta;
  private String preco;

}
