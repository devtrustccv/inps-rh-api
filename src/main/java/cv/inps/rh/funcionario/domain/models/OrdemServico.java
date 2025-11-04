package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OrdemServico {

  private final Long id;
  private final String descricao;
  private final String referente;
  private final Long funId;
  private final Long contratoId;
  private final Long tiprelId;
  private final Long validacaoId;
  private Estado estado;
  private String obs;
  private IdentificadorUnico uuid;

  private OrdemServico(Long id, String descricao, String referente, Long funId,
                       Long contratoId, Long tiprelId, Long validacaoId, Estado estado, String obs, IdentificadorUnico uuid) {
    this.id = id;
    this.descricao = descricao;
    this.referente = referente;
    this.funId = funId;
    this.contratoId = contratoId;
    this.tiprelId = tiprelId;
    this.validacaoId = validacaoId;
    this.estado = estado;
    this.obs = obs;
    this.uuid = uuid;
  }

  public static OrdemServico create(String descricao, String referente,
                                    Long funId, Long contratoId, Long tiprelId, Long validacaoId) {
    return new OrdemServico(null, descricao, referente, funId, contratoId, tiprelId, validacaoId, Estado.A, "obs", IdentificadorUnico.create());
  }

  public static OrdemServico rebuild(Long id, String descricao, String referente,
                                     Long funId, Long contratoId, Long tiprelId, Long validacaoId, Estado estado, String obs, UUID uuid) {
    return new OrdemServico(id, descricao, referente, funId, contratoId, tiprelId, validacaoId, estado, obs, IdentificadorUnico.from(uuid));
  }

}
