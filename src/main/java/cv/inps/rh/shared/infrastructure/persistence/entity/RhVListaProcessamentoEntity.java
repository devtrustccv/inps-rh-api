package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_LISTA_PROCESSAMENTO")
public class RhVListaProcessamentoEntity {

  @Id
  @Column(name = "ID")
  private Long id;

  @Size(max = 20)
  @NotNull
  @Column(name = "ESTADO_CODIGO", nullable = false, length = 20)
  private String estadoCodigo;

  @Size(max = 18)
  @Column(name = "ESTADO", length = 18)
  private String estado;

  @Size(max = 7)
  @Column(name = "MES_REFERENCIA", length = 7)
  private String mesReferencia;

  @Size(max = 200)
  @NotNull
  @Column(name = "OBS", nullable = false, length = 200)
  private String obs;

  @Size(max = 150)
  @Column(name = "DIRECAO", length = 150)
  private String direcao;

  @Size(max = 200)
  @Column(name = "CODIGO_CC", length = 200)
  private String codigoCc;

  @Column(name = "QUANTIDADE")
  private Long quantidade;

  @Column(name = "CABIMENTO")
  private Long cabimento;

  @Column(name = "TOTAL")
  private Long total;

  @NotNull
  @Column(name = "DATA_DE", nullable = false)
  private LocalDate dataDe;

  @Column(name = "DATA_ATE")
  private LocalDate dataAte;

  @Column(name = "TIPO_PROCESSAMENTO")
  private String tipoProcessamento;
}
