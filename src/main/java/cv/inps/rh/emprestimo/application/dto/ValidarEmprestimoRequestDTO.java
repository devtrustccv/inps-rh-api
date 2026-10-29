package cv.inps.rh.emprestimo.application.dto;

import jakarta.validation.Valid;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ValidarEmprestimoRequestDTO {

  // SIM | NAO | CORRIGIR; vazio = reenvio de um empréstimo devolvido para correção.
  private String validar;

  private String observacao;

  // Dados do formulário: correções do validador (SIM) ou do reenvio após correção.
  @Valid
  private FundoSocialRequestDTO dados;

  // Recuperação: tipo do empréstimo recuperado (AQUISICAO_VIATURA | FUNDO_SOCIAL), se corrigido.
  private String tipoEmprestimo;
}
