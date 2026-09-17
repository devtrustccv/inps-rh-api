/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PrestadorAvaliacaoResponseDTO {

  private UUID uuid;
  private UUID missaoUuid;
  private Long nrMissao;
  private String nrMissaoFormatado;       // "nr/ano"
  private String tipoProcesso;
  private String sistemaQualidade;
  private String prazoFornecimento;
  private String qualidadeProduto;
  private String capacidadeResposta;
  private String preco;
  private BigDecimal total;
  private String designacao;              // classe A–D
  private LocalDate dataRegisto;

}
