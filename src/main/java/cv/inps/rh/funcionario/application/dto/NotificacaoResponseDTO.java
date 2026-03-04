package cv.inps.rh.funcionario.application.dto;

import lombok.Data;

@Data
public class NotificacaoResponseDTO {
    private Long id;
    private String assunto;
    private String corpo;
    private String nomeReceptor;
    private String email;
    private String dataEnvio;
    private String estado;
}
