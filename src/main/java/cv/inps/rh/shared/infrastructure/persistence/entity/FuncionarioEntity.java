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
import cv.inps.rh.shared.application.constants.EstadoFuncionario;
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

  
    @Column(name="url_fotografia")
    private String urlFotografia;

  
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
    private EstadoFuncionario estado;

  
    @Column(name="estado_validacao", length=1)
    private String estadoValidacao;

  


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<ContactoEntity> contactos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<EnderecoEntity> enderecos = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<FamiliarEntity> familiares = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<HabilitacaoLiterariaEntity> habilitacoesLiterarias = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<FormacaoFeitaEntity> formacoesFeitas = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<ExperienciaProfEntity> experienciasProfissionais = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
private List<DadosBancariosEntity> dadosBancarios = new ArrayList<>();


  @OneToMany(mappedBy = "funId", fetch = FetchType.LAZY)
private List<DocumentoEntity> documentos = new ArrayList<>();
}