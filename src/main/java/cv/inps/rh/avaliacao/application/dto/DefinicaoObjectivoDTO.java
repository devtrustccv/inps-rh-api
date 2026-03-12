/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class DefinicaoObjectivoDTO  {

  private Long id;
  private String uuid;
  private List<UUID> funUuids;
  private List<String> nomeColaborador;
  private Integer ano;
  private String semestre;
  private Long institId;
  private String instituicaoNome;
  private Long seccaoId;
  private Long seccaoNome;
  private Long cargoId;
  private Long cargoNome;
  private Long carrPccsId;
  private Long carrPccsNome;

  private List<ObjectivoDTO> objectivos;

  private List<CompetenciaComportamentalDTO> competenciasComportamentais;

  private List<CompetenciaTecnicaDTO> competenciasTecnicas;

  private List<AtitudePessoalDTO> atitudesPessoais;


}
