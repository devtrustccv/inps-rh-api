package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import cv.inps.rh.emprestimo.application.dto.DocumentoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnexarComprovativoPagamentoCommand implements Command {

  
  private DocumentoDTO documento;
  @NotBlank(message = "The field <emprestimoId> is required")
  private String emprestimoId;

}