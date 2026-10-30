package cv.inps.rh.missaoservico.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** Valores por defeito do formulário de nova missão (Autorização Prévia). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class MissaoValoresIniciaisDTO {

  /** Nome do utilizador do login (null se o pedido não traz utilizador). */
  private String autorizadoPor;

  /** Data corrente. */
  private LocalDate dataAutorizacao;
}
