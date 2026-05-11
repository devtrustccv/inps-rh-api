/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_CONTRATO_VINCULO")
public class ContratoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_contrato")
    @SequenceGenerator(name = "seq_contrato", sequenceName = "SEQ_CONTRATO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotNull(message = "estado is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private Estado estado;


  @Column(name="data_inicio")
    private LocalDate dataInicio;


  @Column(name="data_fim")
    private LocalDate dataFim;


  @Column(name="duracao")
    private Integer duracao;


  @Column(name="versao")
    private Integer versao;


  @Column(name="tipo_situacao")
    private String tipoSituacao;


  @Column(name="obs")
    private String obs;


  @Column(name="uuid")
    private UUID uuid;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinculo_id", referencedColumnName = "id")
    private ParamVinculoEntity vinculoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_contrato_id", referencedColumnName = "id")
    private ParamContratoEntity tpContratoId;


  @OneToMany(mappedBy = "contratoId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<ContratoEntity> contratosFilhos = new ArrayList<>();


  @OneToMany(mappedBy = "contrVinculoId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<SituacaoLaboralEntity> situacoesLaborais = new ArrayList<>();


  @OneToMany(mappedBy = "contrVinculoId", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
private List<CarreiraEntity> carreiras = new ArrayList<>();   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "contrato_id")
   private ContratoEntity contratoId;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;

  @OneToMany(mappedBy = "contratoId", fetch = FetchType.LAZY)
private List<ContratoHistoricoEntity> historicos = new ArrayList<>();


}
