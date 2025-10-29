package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class DefPagamento {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Contrato contrato;
  private TiposRelacionamento tiprel;
  private BigDecimal valor;
  private TipoMovimento tipoMovimento;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Estado estado;
  private String obs;

  // Construtores privados
  private DefPagamento(Long id,
                       IdentificadorUnico uuid,
                       Contrato contrato,
                       TiposRelacionamento tiprel,
                       BigDecimal valor,
                       TipoMovimento tipoMovimento,
                       LocalDate dataInicio,
                       LocalDate dataFim,
                       Estado estado,
                       String obs) {
    this.id = id;
    this.uuid = uuid;
    this.contrato = contrato;
    this.tiprel = tiprel;
    this.valor = valor;
    this.tipoMovimento = tipoMovimento;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.estado = estado;
    this.obs = obs;
  }

  public static DefPagamento rebuild(Long id,
                                     UUID uuid,
                                     Contrato contrato,
                                     TiposRelacionamento tiprel,
                                     BigDecimal valor,
                                     TipoMovimento tipoMovimento,
                                     LocalDate dataInicio,
                                     LocalDate dataFim,
                                     Estado estado,
                                     String obs) {
    return new DefPagamento(
        id,
        IdentificadorUnico.from(uuid),
        contrato,
        tiprel,
        valor,
        tipoMovimento,
        dataInicio,
        dataFim,
        estado,
        obs
    );
  }

  public static DefPagamento create(Contrato contrato,
                                    TiposRelacionamento tiprel,
                                    BigDecimal valor,
                                    TipoMovimento tipoMovimento,
                                    LocalDate dataInicio,
                                    LocalDate dataFim,
                                    String obs) {
    return new DefPagamento(
        null,
        IdentificadorUnico.create(),
        contrato,
        tiprel,
        valor,
        tipoMovimento,
        dataInicio,
        dataFim,
        Estado.P,
        obs
    );
  }

  public void update(BigDecimal valor,
                     TipoMovimento tipoMovimento,
                     LocalDate dataInicio,
                     LocalDate dataFim,
                     Estado estado,
                     String obs) {
    if (valor != null) this.valor = valor;
    if (tipoMovimento != null) this.tipoMovimento = tipoMovimento;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (estado != null) this.estado = estado;
    if (obs != null) this.obs = obs;
  }
}
