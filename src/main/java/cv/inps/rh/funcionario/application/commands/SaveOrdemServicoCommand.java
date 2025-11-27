package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveOrdemServicoCommand implements Command {

  @NotNull(message = "The field <ordemServico> is required")
  private MultipartFile ordemServico;
  @NotBlank(message = "The field <funcionarioId> is required")
  private String funcionarioId;

}
