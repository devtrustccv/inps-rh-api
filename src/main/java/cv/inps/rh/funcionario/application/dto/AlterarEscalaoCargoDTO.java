/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Melhoria 2.2.1 — "Alterar Escalão / Cargo" (Gestão Laboral). Só para colaboradores com salário do
 * PCCS mas SEM carreira (o escalão vive no tiprel, não numa RH_T_CARREIRA).
 *
 * <ul>
 *   <li>{@code tipoAlteracao}: multiselect do domínio TIPO_MOV_LABORAL / referência GESTAO_LABORAL
 *       (valores ESCALAO e/ou CARGO).</li>
 *   <li>Alterar CARGO é imediato; alterar ESCALÃO vai a validação (fecha o salário anterior e abre um
 *       novo RH_T_DEF_REMUNERACOES, re-associando os RH_T_TIPREL_REM_PAG).</li>
 *   <li>{@code validar}: usado no endpoint de validação (SIM/NAO/CORRIGIR).</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AlterarEscalaoCargoDTO {

  private List<String> tipoAlteracao = new ArrayList<>();

  private Long novoEscalaoId;

  private Long novoCargoId;

  private LocalDate dataInicio;

  private String observacao;

  private EstadoValidacao validar;

}
