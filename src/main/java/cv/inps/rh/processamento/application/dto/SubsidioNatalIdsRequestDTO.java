package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Subsídios de Natal selecionados na lista (Ativar/Inativar e Validar em lote). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class SubsidioNatalIdsRequestDTO {

  @NotEmpty(message = "Selecione pelo menos um subsídio.")
  private List<Long> subsidioIds = new ArrayList<>();
}
