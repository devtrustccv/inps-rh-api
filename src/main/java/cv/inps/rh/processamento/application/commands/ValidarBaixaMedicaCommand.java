package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.processamento.application.dto.BaixaMedicaReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarBaixaMedicaCommand implements Command {

    private BaixaMedicaReqDTO baixamedicareq;

    @NotBlank(message = "The field <validar> is required")
    private String validar;

    @NotBlank(message = "The field <pedidoId> is required")
    private String pedidoId;
}
