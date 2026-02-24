package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.emprestimo.application.dto.BaseDecisaoDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificarPedidoAdiantamentoCommand implements Command {


  private BaseDecisaoDTO basedecisao;
  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}
