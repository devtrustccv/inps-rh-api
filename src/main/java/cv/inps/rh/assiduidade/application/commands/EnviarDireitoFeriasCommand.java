package cv.inps.rh.assiduidade.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarDireitoFeriasCommand implements Command {

  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

  /** Ano do direito; sem valor usa o ano corrente. */
  private Integer ano;

}
