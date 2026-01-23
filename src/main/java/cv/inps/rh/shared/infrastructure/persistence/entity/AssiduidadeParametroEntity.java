package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_ASSIDUIDADE_PARAMETRO")
public class AssiduidadeParametroEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_ASSIDUIDADE_PARAMETRO_id_gen")
  @SequenceGenerator(name = "RH_T_ASSIDUIDADE_PARAMETRO_id_gen", sequenceName = "RH_SEQ_ASSIDUIDADE_PARAM", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 20)
  @Column(name = "DIARIA", length = 20)
  private String diaria;

  @Size(max = 20)
  @NotNull
  @Column(name = "H_INICIO", nullable = false, length = 20)
  private String hInicio;

  @Size(max = 20)
  @NotNull
  @Column(name = "H_FIM", nullable = false, length = 20)
  private String hFim;

  @Size(max = 20)
  @Column(name = "C_ATRASO", length = 20)
  private String cAtraso;

  @Size(max = 20)
  @NotNull
  @Column(name = "AL_HORA_INICIO", nullable = false, length = 20)
  private String alHoraInicio;

  @Size(max = 20)
  @NotNull
  @Column(name = "AL_HORA_FIM", nullable = false, length = 20)
  private String alHoraFim;

  @Size(max = 20)
  @NotNull
  @Column(name = "AL_DURACAO", nullable = false, length = 20)
  private String alDuracao;

  @Size(max = 20)
  @NotNull
  @Column(name = "T_ATRASO", nullable = false, length = 20)
  private String tAtraso;

  @Size(max = 20)
  @Column(name = "TA_COMPENSACAO", length = 20)
  private String taCompensacao;

  @Size(max = 20)
  @Column(name = "T_DISPENSA", length = 20)
  private String tDispensa;

  @Size(max = 20)
  @NotNull
  @Column(name = "HE_PARTIR_DE", nullable = false, length = 20)
  private String hePartirDe;

  @NotNull
  @Column(name = "HE_DIARIA", nullable = false)
  private String heDiaria;

  @Size(max = 20)
  @Column(name = "HE_MENSAL", length = 20)
  private String heMensal;

  @NotNull
  @Column(name = "HE_VALOR_DUTIL", nullable = false, precision = 5, scale = 2)
  private BigDecimal heValorDutil;

  @NotNull
  @Column(name = "HE_VALOR_DNUTIL", nullable = false, precision = 5, scale = 2)
  private BigDecimal heValorDnutil;

  @ColumnDefault("SYSDATE")
  @Column(name = "DT_REGISTO")
  private LocalDate dtRegisto;

  @NotNull
  @Column(name = "USR_REGISTO", nullable = false)
  private Long usrRegisto;

  @Column(name = "DT_FIM")
  private LocalDate dtFim;

  @Column(name = "USR_FIM")
  private Long usrFim;

  @Size(max = 1)
  @ColumnDefault("'A'")
  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "USR_UPDATE")
  private Long usrUpdate;

  @Size(max = 20)
  @Column(name = "HE_ANUAL", length = 20)
  private String heAnual;

  @Size(max = 20)
  @Column(name = "T_MOV_IRREGULAR", length = 20)
  private String tMovIrregular;

  @Size(max = 20)
  @Column(name = "T_ATRASO_APLI_1", length = 20)
  private String tAtrasoApli1;

  @Size(max = 20)
  @Column(name = "T_ATRASO_2", length = 20)
  private String tAtraso2;

  @Size(max = 20)
  @Column(name = "T_ATRASO_APLI_2", length = 20)
  private String tAtrasoApli2;

  @Column(name = "PRAZO_JUSTIF_FALTA")
  private Integer prazoJustifFalta;

  @Column(name = "PRAZO_JUSTIF_AUSENCIA")
  private Integer prazoJustifAusencia;

  @Column(name = "FALTA_MAX_MARCACAO")
  private Integer faltaMaxMarcacao;

  @Column(name = "FALTA_DIREITO_ANULA")
  private Integer faltaDireitoAnula;

  @Column(name = "FALTA_DATA_VENCIMENTO")
  private LocalDate faltaDataVencimento;

  @Column(name = "FALTA_MES_MAXIMO_ANO_1")
  private Integer faltaMesMaximoAno1;

  @Column(name = "MAX_ACUMULACAO")
  private Integer maxAcumulacao;
}
