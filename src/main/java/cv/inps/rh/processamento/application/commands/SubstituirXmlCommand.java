package cv.inps.rh.processamento.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubstituirXmlCommand implements Command {

  @NotBlank(message = "The field <mesReferencia> is required")
  private String mesReferencia;
  @NotNull(message = "The field <fosId> is required")
  private Long fosId;

}
