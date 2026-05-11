/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;

import java.math.BigDecimal;
import java.util.UUID;

@IgrpDTO
public record PesquisaColaboradorResponseDTO(

    Long relacionamentoId,

    UUID funcionarioId,

    String nome,

    String numeroDocumento,

    String direcao,

    String seccao,

    Long direcaoId,

    String centroCusto,

    String categoria,

    String carreira,

    BigDecimal salarioBrutoCategoria,

    String funcao,

    BigDecimal salarioBrutoFuncao,

    Integer nivelReferencia,

    String escalao
) {
}
