package cv.inps.rh.emprestimo.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.emprestimo.application.dto.PlanoFinanceiroRowDTO;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GerarPlanoFinanceiroCommandHandler implements CommandHandler<GerarPlanoFinanceiroCommand, ResponseEntity<List<PlanoFinanceiroRowDTO>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GerarPlanoFinanceiroCommandHandler.class);

  private final EmprestimoWriteService emprestimoWriteService;

  public GerarPlanoFinanceiroCommandHandler(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @IgrpCommandHandler
  public ResponseEntity<List<PlanoFinanceiroRowDTO>> handle(GerarPlanoFinanceiroCommand command) {

    LOGGER.debug("GerarPlanoFinanceiroCommand : {}", command);

    var data = emprestimoWriteService.generateSaveFinancialPlan(command.getEmprestimoId());

    return ResponseEntity.ok(data);
  }

}
