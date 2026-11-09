package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Parametrização de bancos (RH_T_BANCO). O tmId fica de fora por agora. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class BancoRequestDTO {

  @NotBlank(message = "O código do banco é obrigatório")
  @Size(max = 10, message = "O código do banco não pode ter mais de 10 caracteres")
  private String codigo;

  @Size(max = 10, message = "A sigla não pode ter mais de 10 caracteres")
  private String sigla;

  @NotBlank(message = "O nome do banco é obrigatório")
  @Size(max = 100, message = "O nome do banco não pode ter mais de 100 caracteres")
  private String nome;

  private Long numeroConta;

  private String nib;

  /** Entidade do SIGOF (INPSSIGOF.ENTIDADES). */
  private Long entId;

}
