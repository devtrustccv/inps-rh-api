/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Uma linha do ecrã: o prestador num processo (o processo vem do próprio prestador, por isso não se
 * envia o tipo). Valores do domínio AVALIACAO_FORNECEDOR: 100, 75, 50 ou 25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoAvaliacaoItemRequestDTO {

  private UUID missaoPrestUuid;           // RH_T_MISSAO_PRESTADOR.UUID — obrigatório
  private String sistemaQualidade;
  private String prazoFornecimento;
  private String qualidadeProduto;
  private String capacidadeResposta;
  private String preco;

}
