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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class JustificarFaltaDTO  {


  private UUID colaboradorId ;


  private String nomeColaborador ;

  @Valid
  private List<FaltaItemDTO> itensFalta = new ArrayList<>();


  private String parecerResponsavel ;


  private Long responsavelId ;


  private String obsResponsavel ;


  private String despachoRh ;


  private Long tipoJustificacao ;

  /** "Deduzir Falta Em" — DOMAIN TP_DESCONTO_FALTA: FERIAS | DISPENSA. */
  private String deduzirFaltaEm ;

  /** Só de resposta: valor da falta por dia. */
  private BigDecimal valorDiario ;

  /** Só de resposta: soma dos dias justificados. */
  private BigDecimal valorTotal ;

  /**
   * Documentos comprovativos do bloco "Justificar Faltas Selecionadas" — o formulário
   * permite anexar vários ("Adicionar outro documento"). Aplicam-se a todas as faltas
   * seleccionadas; {@code FaltaItemDTO.documento} continua a servir o anexo de um dia
   * específico.
   */
  @Valid
  private List<AnexoReqDTO> documentos = new ArrayList<>();


  private EstadoValidacao validar ;


  private Integer ano ;


  private Integer mes ;

  private String tipoOrdemServico ;

}
