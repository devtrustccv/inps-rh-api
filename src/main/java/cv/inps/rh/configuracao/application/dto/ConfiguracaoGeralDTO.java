/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor


@IgrpDTO
public class ConfiguracaoGeralDTO  {



  private String id ;
  @NotBlank(message = "The field <jornadaDiaria> is required")

  private String jornadaDiaria ;
  @NotBlank(message = "The field <jornadaDiariaInicio> is required")

  private String jornadaDiariaInicio ;
  @NotBlank(message = "The field <jornadaDiariaFim> is required")

  private String jornadaDiariaFim ;
  @NotBlank(message = "The field <almocoInicio> is required")

  private String almocoInicio ;
  @NotBlank(message = "The field <almocoFim> is required")

  private String almocoFim ;
  @NotBlank(message = "The field <almocoDuracao> is required")

  private String almocoDuracao ;


  private String primeiroAtrazo ;


  private String faltaAplicadaPrimeiroAtrazo ;


  private String segundoAtrazo ;


  private String faltaAplicadaSegundoAtrazo ;


  private String movimentoIrregular ;
  @NotBlank(message = "The field <horaAPartirDe> is required")

  private String horaAPartirDe ;


  private String limiteDiario ;
  @NotNull(message = "The field <percentagemDiasUteis> is required")

  private BigDecimal percentagemDiasUteis ;


  private BigDecimal percentagemDiasNaoUteis ;


  private Integer periodoLimiteJustFalta ;


  private Integer prazoLimiteJustAusencia ;


  private Integer numeroMaximoMarcAno ;


  private Integer direitoAnual ;


  private Integer maximoAcumulacao ;


  private LocalDate dataVencimentoFerias ;


  private Integer numeroMesesLimiteTrabalho ;

}
