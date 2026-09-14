/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class PrestadorServicoRequestDTO {

  private Long entId;                     // INPSSIGOF.ENTIDADES.ID — obrigatório
  private String nome;                    // omitido: nome da entidade
  private String nif;
  private String email;                   // email principal — obrigatório
  private String telefone;
  private Long ilhaId;                    // GLB_T_GEOGRAFIA.ID
  private String morada;
  private String estado;                  // A | I — no registo, por defeito A
  private List<PrestadorEmailRequestDTO> emails;  // outros emails; null = não mexer

}
