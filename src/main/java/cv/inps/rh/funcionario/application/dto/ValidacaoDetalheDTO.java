package cv.inps.rh.funcionario.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Uma linha da grelha "Detalhe de alterações" (spec DOSSIÊ, linha 1559).
 *
 * <p>Os cinco primeiros campos são exatamente as colunas que a spec define. O {@code tabelaName} é
 * acrescento: quando uma validação atravessa várias tabelas (contrato = contrato + tiprel +
 * vínculo), sem ele o validador vê campos soltos sem saber a que registo pertencem. O frontend
 * pode ignorá-lo se não quiser agrupar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ValidacaoDetalheDTO {

  @Schema(description = "Nome do campo que foi alterado")
  private String campoAlterado;

  @Schema(description = "Valor que o campo tinha antes")
  private String valorAnterior;

  @Schema(description = "Valor que o campo tem agora")
  private String valorNovo;

  @Schema(description = "Utilizador responsável pela alteração")
  private String alteradoPor;

  @Schema(description = "Data em que a alteração foi registada")
  private String dataAlteracao;

  @Schema(description = "Tabela a que o campo pertence; permite agrupar quando a validação toca em várias")
  private String tabelaName;
}
