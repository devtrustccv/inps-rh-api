package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Linha da parametrização da ajuda de custo (Outras Configurações → Missão). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ParamAjudaCustoDTO {

  private String id;                      // uuid (vazio = novo)

  @NotNull(message = "Indique a Carreira / cargo.")
  private Long carreiraId;                // RH_T_PARAM_CARREIRA.ID
  private String carreiraDesc;

  private BigDecimal valorNacional;       // Valor AC Nacional (diário)
  private BigDecimal valorEstrangeiro;    // Valor AC Estrangeiro (diário)

  private LocalDate dataInicio;           // vazio = hoje
  private LocalDate dataFim;

  private String estado;                  // A | I (vazio = A)
}
