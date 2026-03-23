/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class AjudaCustoRequestDTO {

  private UUID colaboradorId;             // MissaoColaboradorEntity.id — obrigatório
  private Boolean flgAlojamento;          // checkbox "Inclui alojamento?" — obrigatório
  private Integer numeroDiasAlojamento;   // número de dias — obrigatório
  // valorDiario → calculado no service pela parametrização:
  //   100% se colaborador tem alojamento próprio (flgAlojamento = false)
  //   2/3  se empresa paga alojamento sem alimentação
  //   1/3  se empresa paga alojamento com alimentação
  // valorTotal = valorDiario × numeroDiasAlojamento → calculado no service
  private BigDecimal valorDiario;         // calculado pelo service
  private BigDecimal valorTotal;

}
