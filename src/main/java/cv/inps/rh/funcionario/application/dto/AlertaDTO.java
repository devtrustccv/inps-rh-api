package cv.inps.rh.funcionario.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AlertaDTO {
    private Long id;
    private UUID uuid;
    private String referenciaName;
    private Long referenciaId;
    private UUID referenciaUuid;
    private UUID funcionarioId;
    private String descricao;
    private String estado;
    private String tipoSituacao;
    private String tipoAlerta;
    private String prioridade;
    private String dataRegisto;
}
