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
public class PrestadorServicoResponseDTO {

  private Long id;
  private UUID uuid;
  private Long entId;
  private String nome;
  private String nif;
  private String email;
  private String telefone;
  private Long ilhaId;
  private String ilhaNome;
  private String morada;
  private String estado;
  private String estadoDesc;              // "Activo" | "Inactivo"
  private List<PrestadorEmailResponseDTO> emails;
  // audit
  private LocalDate dataRegisto;
  private String userRegistoName;
  private LocalDate dataAlteracao;
  private String userAlteracaoName;

}
