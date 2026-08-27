package cv.inps.rh.funcionario.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo do PATCH de ativar/desativar contrato.
 * estado: "A" (ativar) ou "I" (desativar).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlterarEstadoContratoDTO {

  @NotBlank(message = "O campo <estado> é obrigatório (A para ativar, I para desativar).")
  private String estado;

}
