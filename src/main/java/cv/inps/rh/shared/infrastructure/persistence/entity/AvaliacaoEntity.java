package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "RH_T_AVD")
@Getter
@Setter
public class AvaliacaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd")
  @SequenceGenerator(name = "seq_avd", sequenceName = "SEQ_AVD", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUN_ID")
  private FuncionarioEntity funcionario;

  @Column(name = "ANO", nullable = false)
  private Integer ano;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private DirecaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_ID")
  private ParamCarreiraEntity carreira;

  /** INPS | DIRECAO | INDIVIDUAL — ver AbrangenciaAvaliacao. */
  @Column(name = "ABRAGENCIA", length = 100)
  private String abrangencia;

  @Column(name = "ESTADO")
  private String estado;                       // 'A' | 'P' | 'C'

  @Column(name = "AVALIACAO_FINAL")
  private Double avaliacaoFinal;

  @Column(name = "PESO_COMPORTAMENTAIS")
  private BigDecimal pesoComportamentais;

  @Column(name = "PESO_TECNICA")
  private BigDecimal pesoTecnica;

  // Resultados, entrevista e pareceres vivem por período em RH_T_AVD_DETALHE (AvaliacaoDetalheEntity):
  // as colunas AVALIACAO_OBJECTIVO ... OBS_COMISSAO_EXEC foram removidas de RH_T_AVD (spec BD 21/09/2026)
  // e mapeá-las aqui fazia qualquer leitura da avaliação falhar com ORA-00904 (listas a 500).

  @Column(name = "UUID")
  private UUID uuid;

  @OneToMany(mappedBy = "avaliacaoObj", cascade = CascadeType.ALL)
  private List<AvaliacaoObjectivoEntity> objetivos;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoCompetenciaEntity> competencias;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoAtitudePessoalEntity> atitudesPessoais;

  /** Um detalhe por período do ciclo — é aqui que vivem os resultados. */
  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoDetalheEntity> detalhes;
}
