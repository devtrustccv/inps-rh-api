package cv.inps.rh.funcionario.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.dto.OrdemServicoItemReqDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalvarOrdemServicoCommand implements Command {

  @NotBlank(message = "The field <funcionarioUuid> is required")
  private String funcionarioUuid;

  private List<OrdemServicoItemReqDTO> items;

}
