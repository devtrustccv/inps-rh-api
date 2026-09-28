package cv.inps.rh.avaliacao.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoObjectivosComunsDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoObjectivosComunsCommand implements Command {


  private AvaliacaoObjectivosComunsDTO avaliacaoobjectivoscomuns;
  @NotNull(message = "The field <ano> is required")
  private Integer ano;
  @NotBlank(message = "The field <periodicidade> is required")
  private String periodicidade;

}
