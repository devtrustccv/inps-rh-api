/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoLogisticaResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private MissaoProcessoResponseDTO processo;
  private LocalDate dataInicioMissao;     // valor por defeito das datas do alojamento
  private LocalDate dataFimMissao;
  // Só a secção do tipo do processo vem preenchida; as restantes vêm vazias
  private List<BilhetePassagemResponseDTO> bilhetesPassagem;
  private List<SeguroViagemResponseDTO> segurosViagem;
  private List<AlojamentoResponseDTO> alojamentos;
  private List<AjudaCustoResponseDTO> ajudasCusto;
  // Colaboradores que podem entrar numa linha; no bilhete/alojamento só os que têm requisição, com o prestador
  private List<MissaoColaboradorResponseDTO> colaboradoresDisponiveis;
  private MissaoNotificacaoResponseDTO notificacao;
  private String executadoPor;
  private LocalDate dataExecucao;

}
