package cv.inps.rh.missaoservico.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSubmissaoServicoCommand implements Command {


  private MissaoSubmissaoRequestDTO missaosubmissaorequest;
  @NotBlank(message = "The field <uuid> is required")
  private String uuid;

}
