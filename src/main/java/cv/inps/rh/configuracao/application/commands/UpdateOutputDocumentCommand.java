package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.configuracao.application.dto.DocOutputRequestDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOutputDocumentCommand implements Command {


  private DocOutputRequestDTO docoutputrequest;
  @NotBlank(message = "The field <id> is required")
  private String id;

}
