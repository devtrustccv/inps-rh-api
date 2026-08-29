/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Melhoria 2.2.1 — "Alterar Escalão / Cargo" (referência de validação ALTERACAO_ESCALAO). Só para
 * colaboradores com salário do PCCS mas SEM carreira (o escalão vive no tiprel, não numa RH_T_CARREIRA).
 *
 * <ul>
 *   <li>{@code tipoAlteracao}: multiselect do domínio TIPO_MOV_LABORAL (ESCALAO_NOVO/CARGO_NOVO).
 *       O frontend envia os valores selecionados <b>separados por vírgulas</b> (ex.: "ESCALAO_NOVO,CARGO_NOVO");
 *       gravam-se tal-e-qual em RH_T_TIPOS_RELACIONAMENTO.TIPO_SITUACAO (ver spec Mobilidade análoga).</li>
 *   <li>{@code dataInicio}/{@code dataFim}: gravam em RH_T_TIPOS_RELACIONAMENTO.DATA_INICIO/DATA_FIM.</li>
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

  private String tipoAlteracao;

  private Long novoEscalaoId;

  private Long novoCargoId;

  private LocalDate dataInicio;

  private LocalDate dataFim;

  private String observacao;

  private EstadoValidacao validar;

}
