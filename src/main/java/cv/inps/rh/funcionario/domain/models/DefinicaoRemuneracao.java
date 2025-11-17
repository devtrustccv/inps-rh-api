package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.funcionario.application.dto.RenumeracaoListDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.TipoMovimento;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class DefinicaoRemuneracao {

  private final Long id;
  private final IdentificadorUnico uuid;
  private BigDecimal percentagem;
  private BigDecimal valor;
  private Estado estado;
  private String obs;
  private TipoMovimento tipoMovimento;
  private LocalDate dataInicio;
  private LocalDate dataFim;


  private DefinicaoRemuneracao(Long id,
                               IdentificadorUnico uuid,
                               BigDecimal percentagem,
                               BigDecimal valor,
                               Estado estado,
                               String obs,
                               TipoMovimento tipoMovimento,
                               LocalDate dataInicio,
                               LocalDate dataFim) {
    this.id = id;
    this.uuid = uuid;
    this.percentagem = percentagem;
    this.valor = valor;
    this.estado = estado;
    this.obs = obs;
    this.tipoMovimento = tipoMovimento;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
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
        percentagem,
        valor,
        Estado.P,
        obs,
        tipoMovimento,
        LocalDate.now(),
        LocalDate.now());
  }

  // Rebuild: reconstruir do repositório
  public static DefinicaoRemuneracao rebuild(Long id,
                                             UUID uuid,
                                             BigDecimal percentagem,
                                             BigDecimal valor,
                                             Estado estado,
                                             String obs,
                                             TipoMovimento tipoMovimento,LocalDate dataInicio, LocalDate dataFim) {
    return new DefinicaoRemuneracao(
        id,
        IdentificadorUnico.from(uuid),
        percentagem,
        valor,
        estado,
        obs,
        tipoMovimento,
        dataInicio,
        dataFim
    );
  }

  // Atualizar
  public void update(BigDecimal percentagem,
                     BigDecimal valor,
                     String obs,
                     TipoMovimento tipoMovimento) {
    if (percentagem != null) this.percentagem = percentagem;
    if (valor != null) this.valor = valor;
    if (obs != null) this.obs = obs;
    if (tipoMovimento != null) this.tipoMovimento = tipoMovimento;
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  public void mudarEstado(Estado estado) {
    this.estado = estado;
  }

}
