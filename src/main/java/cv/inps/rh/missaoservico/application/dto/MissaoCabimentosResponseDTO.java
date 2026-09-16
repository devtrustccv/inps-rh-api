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

/**
 * Cabimentos de toda a missão — vista de consulta do ecrã "Cabimentação", que mostra os quatro tipos
 * de serviço numa só tabela. Cabimentar continua a ser por processo
 * ({@code /processos/{tipoProcesso}/cabimento}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoCabimentosResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private String estadoMissao;                        // A | I | FINALIZADO
  private List<MissaoProcessoResponseDTO> processos;  // etapa de cada processo activo
  private List<ProcessoCabimentoItemResponseDTO> itens;  // linhas dos quatro processos; referencia = tipo de serviço
  private BigDecimal valorTotal;                      // soma de todas as linhas
  private LocalDate dataExecucao;

}
