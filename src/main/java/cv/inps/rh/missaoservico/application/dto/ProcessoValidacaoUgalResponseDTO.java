/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ProcessoValidacaoUgalResponseDTO {

  private UUID missaoUuid;
  private String nrMissaoFormatado;
  private MissaoProcessoResponseDTO processo;
  // "Documento do Processo" (spec): os três tipos de documento a validar
  private List<AnexoRespDTO> autorizacao;     // anexos do registo da missão
  private List<AnexoRespDTO> requisicoes;     // PDFs das requisições do processo
  private List<AnexoRespDTO> faturas;         // anexos das linhas de logística
  private ParecerResponseDTO parecerAtual;    // rascunho ou parecer emitido no ciclo actual
  private List<ParecerResponseDTO> historico; // todos os pareceres UGAL, incluindo os anulados

}
