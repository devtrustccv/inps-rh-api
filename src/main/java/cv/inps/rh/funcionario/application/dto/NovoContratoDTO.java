/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class NovoContratoDTO  {



  private EstadoValidacao validar ;

  @Valid
  private DadosContratuaisReqDTO dadosContratuais ;

  private String tipoOrdemServico ;

  // Opcional: uuid do alerta de origem quando o Novo Contrato é aberto a partir do "Processar" de um
  // alerta de CONVERSAO_CONTRATO (JOB Alerta / TRANSVERSAL 3.4.2 §2). Quando presente, o registo marca
  // o alerta como tratado (flg_tratamento='S'); o resto do fluxo de Novo Contrato é indiferente a ele.
  private UUID alertaId ;

}
