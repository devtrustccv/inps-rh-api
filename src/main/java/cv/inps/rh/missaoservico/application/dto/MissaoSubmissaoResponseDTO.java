/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
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
public class MissaoSubmissaoResponseDTO  {

  private Long id;
  private UUID uuid;
  private Long nrMissao;                  // sequencial dentro do ano
  private Integer ano;                    // ano de criação
  private String nrMissaoFormatado;       // "nr/ano" — ex.: "1/2026"
  private String etapaAtual;
  private String etapaAtualDesc;          // descrição legível da etapa — ex.: "Processamento Logístico"
  private Long paisDestinoId;
  private String paisDestinoNome;
  private Integer flgDestino;
  private String descricaoDestino;        // local preciso da missão
  private String ambitoMissao;            // texto livre: objetivo/âmbito da missão
  private String tipoDestino;             // "NACIONAL" | "INTERNACIONAL" — derivado de flgDestino
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer nrDias;
  private String autorizadoPor;
  private LocalDate dataAutorizacao;
  private String etapa;
  private String estado;
  private List<MissaoColaboradorResponseDTO> colaboradores;
  private List<AnexoRespDTO> documentos;
  // audit
  private LocalDate dataRegisto;
  private Long userRegistoId;
  private String userRegistoName;
  private Long userAlteracaoId;
  private String userAlteracaoName;
  private LocalDate dataAlteracao;

}
