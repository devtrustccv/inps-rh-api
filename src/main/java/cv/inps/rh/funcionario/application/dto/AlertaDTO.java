package cv.inps.rh.funcionario.application.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AlertaDTO {
    private Long id;
    private UUID uuid;
    /** RH_T_ALERTA.REFERENCIA (coluna Referência da lista — QA TR-23). */
    private String referencia;
    private String referenciaName;
    private Long referenciaId;
    private UUID referenciaUuid;
    private UUID funcionarioId;
    private String descricao;
    private String estado;
    private String tipoSituacao;
    private String tipoAlerta;
    /** Descrição do tipo (domínio TIPO_ALERTA_NOTIFICACAO — QA TR-24). */
    private String tipoAlertaDescricao;
    /** RH_T_ALERTA.FLG_NOTIFICACAO (S/N): o botão Ver Notificação só faz sentido com S (QA TR-25). */
    private String flgNotificacao;
    private String prioridade;
    private String dataRegisto;
}
