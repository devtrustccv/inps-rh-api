/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoPrestadorResponseDTO {

  private UUID missaoPrestUuid;
  private String nomePrestador;
  private String nrMissaoFormatado;
  private String tipoProcesso;
  private Boolean podeAvaliar;            // só prestadores com requisição activa no processo
  private Boolean avaliado;
  private List<CriterioAvaliacaoResponseDTO> criterios;
  private BigDecimal total;
  private String designacao;              // A | B | C | D
  private String designacaoDesc;          // "Fornecedor Preferencial", …
  private List<OpcaoDominioResponseDTO> opcoesAvaliacao;  // para os selects: 100 Muito Bom, 75 Bom, …
  private String executadoPor;
  private LocalDate dataExecucao;

}
