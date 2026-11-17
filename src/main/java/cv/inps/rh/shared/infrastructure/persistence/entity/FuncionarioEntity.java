/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FUNCIONARIOS")
public class FuncionarioEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_funcionario")
    @SequenceGenerator(name = "seq_funcionario", sequenceName = "SEQ_FUNCIONARIO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="uuid")
    private UUID uuid;


    @NotNull(message = "tipoDocumentoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_documento_id", referencedColumnName = "id")
    private TipoDocumentoEntity tipoDocumentoId;
    @Column(name="num_documento")
    private String numDocumento;


    @Column(name="nome")
    private String nome;

  @Column(name = "nome_normalizado")
  private String nomeNormalizado;


    @Column(name="fotografia")
    private String fotografia;


    @Column(name="data_nascimento")
    private LocalDate dataNascimento;


    @Column(name="sexo")
    private String sexo;


    @Column(name="nm_mae")
    private String nmMae;


    @Column(name="nm_pai")
    private String nmPai;


    @Column(name="estado_civil")
    private String estadoCivil;


    // Texto da nacionalidade (GLB_T_GEOGRAFIA.NACIONALIDADE) — derivado de nacionId na gravação.
    @Column(name="nacionalidade")
    private String nacionalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nacion_id", referencedColumnName = "id")
    private GeografiaEntity nacionId;


    @Column(name="localidade", length=200)
    private String localidade;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loc_nasc_id", referencedColumnName = "id")
    private GeografiaEntity locNascId;
    @Column(name="nif")
    private Long nif;


    @Column(name="nu_seg_inps")
    private String nuSegInps;


    @Column(name="ent_id")
    private Long entId;


    @Column(name="id_colaborador")
    private Long idColaborador;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="estado_validacao", length=1)
    private String estadoValidacao;




  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<ContactoEntity> contactos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<FamiliarEntity> familiares = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<HabilitacaoLiterariaEntity> habilitacoesLiterarias = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<FormacaoFeitaEntity> formacoesFeitas = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<ExperienciaProfEntity> experienciasProfissionais = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<DadosBancariosEntity> dadosBancarios = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<DocumentoEntity> documentos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<TiposRelacionamentoEntity> tiposrelacionamentos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<ContratoEntity> contratos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<MobilidadeEntity> mobilidades = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<RegimeTrabalhoEntity> regimesTrabalhos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<DefinicaoRemuneracaoEntity> definicoesRenumeracoes = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<DefPagamentoEntity> definicoesPagamentos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<ValidacaoEntity> validacoes = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<OrdemServicoEntity> ordemServicos = new ArrayList<>();


  @OneToOne(mappedBy = "funId", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  private EnderecoEntity endereco;

  /**
   * Histórico do documento de identificação. Mudar o tipo ou o número inativa (I) o atual e cria/reativa
   * outro (A); as restantes alterações atualizam a linha atual. O atual também está em
   * {@link #tipoDocumentoId}/{@link #numDocumento}. EAGER + SELECT como o antigo @OneToOne inverso
   * (sempre carregado, sem join).
   */
  @OneToMany(mappedBy = "funId", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SELECT)
  private List<DocumentoPessoalEntity> documentosPessoais = new ArrayList<>();

  /**
   * Documento ATUAL: o não eliminado que coincide com o tipo/número do funcionário (o mais recente);
   * senão o mais recente não inativo; senão o mais recente. Tolera mais de uma linha por funcionário
   * (o import cria BI + passaporte).
   */
  public DocumentoPessoalEntity getDocumentoPessoal() {
    if (documentosPessoais == null || documentosPessoais.isEmpty()) return null;
    Long tipo = tipoDocumentoId != null ? tipoDocumentoId.getId() : null;
    Comparator<DocumentoPessoalEntity> porId =
        Comparator.comparing(DocumentoPessoalEntity::getId, Comparator.nullsLast(Comparator.naturalOrder()));
    var vivos = documentosPessoais.stream().filter(d -> d != null && d.getEstado() != Estado.E).toList();
    return vivos.stream()
        .filter(d -> Objects.equals(d.getNumDocumento(), numDocumento)
            && d.getTipoDocumentoId() != null && Objects.equals(d.getTipoDocumentoId().getId(), tipo))
        .max(porId)
        .or(() -> vivos.stream().filter(d -> d.getEstado() != Estado.I).max(porId))
        .or(() -> vivos.stream().max(porId))
        .orElse(null);
  }

  /** Junta o documento ao histórico (se ainda não estiver) e liga-o a este funcionário. */
  public void setDocumentoPessoal(DocumentoPessoalEntity documento) {
    if (documento == null) return;
    documento.setFunId(this);
    if (documentosPessoais == null) documentosPessoais = new ArrayList<>();
    if (documentosPessoais.stream().noneMatch(d -> d == documento)) documentosPessoais.add(documento);
  }


}
