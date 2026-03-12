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
  private Long funId;
  private UUID funUuid;
  private String nomeColaborador;
  private Integer ano;
  private String semestre;
  private Long institId;
  private Long seccaoId;
  private Long cargoId;
  private Long carrPccsId;

  private List<ObjectivoDTO> objectivos;

  private List<CompetenciaComportamentalDTO> competenciasComportamentais;

  private List<CompetenciaTecnicaDTO> competenciasTecnicas;

  private List<AtitudePessoalDTO> atitudesPessoais;


}
