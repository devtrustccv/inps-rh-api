package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "RH_T_AVD")
@Getter
@Setter
public class AvaliacaoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd")
  @SequenceGenerator(name = "seq_avd", sequenceName = "RH_T_AVD_SEQ", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity funcionario;

  @Column(name = "ANO", nullable = false)
  private Integer ano;

  @Column(name = "SEMESTRE", length = 1, nullable = false)
  private String semestre;                     // '1' | '2'

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private InstituicaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_ID")
  private ParamCarreiraEntity carreira;

  @Column(name = "ESTADO", length = 1, nullable = false)
  private String estado;                       // 'A' | 'P' | 'C'

  @Column(name = "AVALIACAO_FINAL", precision = 5, scale = 2)
  private Double avaliacaoFinal;

  @Column(name = "PESO_COMPORTAMENTAIS", precision = 5, scale = 2)
  private BigDecimal pesoComportamentais;

  @Column(name = "PESO_TECNICA", precision = 5, scale = 2)
  private BigDecimal pesoTecnica;

  @Column(name = "AVALIACAO_OBJECTIVO", precision = 5, scale = 2)
  private BigDecimal avaliacaoObjectivo;

  @Column(name = "AVALIACAO_COMPETENCIA", precision = 5, scale = 2)
  private BigDecimal avaliacaoCompetencia;

  @Column(name = "AVALIACAO_ATITUDE_PESS", precision = 5, scale = 2)
  private BigDecimal avaliacaoAtitudePess;

  @Column(name = "AVALIACAO_QUALITATIVA", length = 100)
  private String avaliacaoQualitativa;

  @Column(name = "OBSERVACAO_GERAL", length = 500)
  private String observacaoGeral;

  @Column(name = "DESCRICAO_PLANO", length = 500)
  private String descricaoPlano;

  @Column(name = "DATA_INICIO_ENTREVISTA")
  private LocalDate dataInicioEntrevista;

  @Column(name = "HORA_INICIO_ENTREVISTA", length = 5)
  private String horaInicioEntrevista;         // formato HH:MM

  @Column(name = "HORA_FIM_ENTREVISTA", length = 5)
  private String horaFimEntrevista;            // formato HH:MM

  @Column(name = "PARECER_COLABORADOR", length = 500)
  private String parecerColaborador;

  @Column(name = "JUSTIFICACAO_MOTIVO", length = 200)
  private String justificacaoMotivo;

  @Column(name = "OBS_COMISSAO_EXEC", length = 500)
  private String obsComissaoExec;

  @Column(name = "UUID")
  private UUID uuid;

  @OneToMany(mappedBy = "avaliacaoObj", cascade = CascadeType.ALL)
  private List<AvaliacaoObjectivoEntity> objetivos;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoCompetenciaEntity> competencias;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoAtitudePessoalEntity> atitudesPessoais;
}
