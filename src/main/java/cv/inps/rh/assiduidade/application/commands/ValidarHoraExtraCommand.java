package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidarHoraExtraCommand implements Command {


  private HoraExtraReqDTO horaextrareq;
  @NotBlank(message = "The field <pedidoId> is required")
  private String pedidoId;

}
