package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class DefinicaoRemuneracao {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Contrato contrato;
  private BigDecimal percentagem;
  private BigDecimal valor;
  private Estado estado;
  private String obs;
  private TipoMovimento tipoMovimento;

  // Construtor privado
  private DefinicaoRemuneracao(Long id,
                               IdentificadorUnico uuid,
                               Contrato contrato,
                               BigDecimal percentagem,
                               BigDecimal valor,
                               Estado estado,
                               String obs,
                               TipoMovimento tipoMovimento) {
    this.id = id;
    this.uuid = uuid;
    this.contrato = contrato;
    this.percentagem = percentagem;
    this.valor = valor;
    this.estado = estado;
    this.obs = obs;
    this.tipoMovimento = tipoMovimento;
  }

  // Factory: criar nova definição
  public static DefinicaoRemuneracao create(Long id,
                                            BigDecimal percentagem,
                                            BigDecimal valor,
                                            String obs,
                                            TipoMovimento tipoMovimento) {
    return new DefinicaoRemuneracao(
        id!=null && id>0 ? id : null ,
        IdentificadorUnico.create(),
        null,
        percentagem,
        valor,
        Estado.P,
        obs,
        tipoMovimento
    );
  }

  public void associate(Contrato contrato) {
    this.contrato = contrato;
  }

  // Rebuild: reconstruir do repositório
  public static DefinicaoRemuneracao rebuild(Long id,
                                             UUID uuid,
                                             Contrato contrato,
                                             BigDecimal percentagem,
                                             BigDecimal valor,
                                             Estado estado,
                                             String obs,
                                             TipoMovimento tipoMovimento) {
    return new DefinicaoRemuneracao(
        id,
        IdentificadorUnico.from(uuid),
        contrato,
        percentagem,
        valor,
        estado,
        obs,
        tipoMovimento
    );
  }

  // Atualizar
  public void update(BigDecimal percentagem,
                     BigDecimal valor,
                     String obs,
                     Estado estado,
                     TipoMovimento tipoMovimento) {
    if (percentagem != null) this.percentagem = percentagem;
    if (valor != null) this.valor = valor;
    if (obs != null) this.obs = obs;
    if (estado != null) this.estado = estado;
    if (tipoMovimento != null) this.tipoMovimento = tipoMovimento;
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }
}
