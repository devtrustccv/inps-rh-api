package cv.inps.rh.emprestimo.interfaces.rest;

import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.emprestimo.domain.service.EmprestimoWriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@IgrpController
@RestController
@RequestMapping(path = "emprestimo")
public class EmprestimoCustomController {

  private final EmprestimoWriteService emprestimoWriteService;

  public EmprestimoCustomController(EmprestimoWriteService emprestimoWriteService) {
    this.emprestimoWriteService = emprestimoWriteService;
  }

  @DeleteMapping
  public ResponseEntity<Void> cancelLoan(String emprestimoId) {

    emprestimoWriteService.cancelLoan(emprestimoId);

    return ResponseEntity.noContent().build();
  }

}
