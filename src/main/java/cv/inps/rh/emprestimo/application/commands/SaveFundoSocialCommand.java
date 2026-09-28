package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.emprestimo.application.dto.FundoSocialRequestDTO;
import cv.inps.rh.emprestimo.domain.service.constants.TipoPedido;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveFundoSocialCommand implements Command {

  private TipoPedido tipoPedido;

  private TipoPedido tipoEmprestimo;

  private List<FundoSocialRequestDTO> fundosocialrequest;

}
