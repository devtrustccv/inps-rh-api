/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.List;
import java.util.ArrayList;


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

  
    @Column(name="nacionalidade")
    private String nacionalidade;

  


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
private List<CarreiraEntity> carreiras = new ArrayList<>();


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


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<SituacaoLaboralEntity> situacoesLaborais = new ArrayList<>();   @OneToOne(mappedBy = "funId")
   private EnderecoEntity endereco;


}