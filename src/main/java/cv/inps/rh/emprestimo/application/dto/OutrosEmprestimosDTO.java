/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.emprestimo.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

@IgrpDTO
public record OutrosEmprestimosDTO(

    String id,

    @NotBlank(message = "<Tipo Emprestimo é obrigatório>")
    String tipoEmprestimo,

    LocalDate dataEmprestimo,

    LocalDate dataTermino,

    BigDecimal valorEmprestimo,

    BigDecimal valorPrestacaoMensal
) {
}
