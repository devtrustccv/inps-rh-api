package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.ValidacaoMovimentoImportadoDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarMovimentoImportadoCommand implements Command {

    private ValidacaoMovimentoImportadoDTO validacaomovimentoimportado;

    @NotBlank(message = "The field <movimentoId> is required")
    private String movimentoId;
}
