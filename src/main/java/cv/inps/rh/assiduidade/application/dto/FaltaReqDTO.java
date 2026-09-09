/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.assiduidade.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class FaltaReqDTO  {



  private UUID colaboradorId ;


  private String colaboradorNome ;


  private LocalDate dataInicio ;


  private LocalDate dataFim ;


  private Integer totalDias ;


  private String totalDeHorasAusentes ;


  /**
   * "Com Justificativo?" do formulário (DOMAIN SIM_NAO). Faz duas coisas: com "SIM" abre os
   * blocos Motivo/Tipo Falta/Anexos/Parecer e cria pedido + RH_T_FALTA; com "NÃO" só se regista
   * a síntese diária e o dia fica na lista por justificar. Gravado em
   * RH_T_FALTA.FLG_JUSTIFICATIVO, o mesmo campo que o ecrã de Justificar Falta preenche.
   */
  private String justificar ;


  private String motivoAusencia ;


  private String parecer ;


  private UUID responsavel ;

  private String responsavelNome ;


  private String observacao ;


  private EstadoValidacao validar ;




  private Long tipoJustificacao ;

  /** "Deduzir Falta Em" — DOMAIN TP_DESCONTO_FALTA: FERIAS | DISPENSA. */
  private String deduzirFaltaEm ;

  /** Só de resposta: valor da falta por dia. */
  private BigDecimal valorDiario ;

  /** Só de resposta: valorDiario x totalDias. */
  private BigDecimal valorTotal ;

  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();

  private String tipoOrdemServico ;

}
