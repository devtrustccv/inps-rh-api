/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FERIAS_GOZADAS")
public class FeriasGozadasEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ferias_gozada")
    @SequenceGenerator(name = "seq_ferias_gozada", sequenceName = "SEQ_FERIAS_GOZADA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "anoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ano_id", referencedColumnName = "id")
    private AnoEntity anoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="tiprel_id_susbtituido")
    private Long tiprelIdSusbtituido;

  
    @Column(name="motivo_alteracao")
    private String motivoAlteracao;

  
    @Column(name="obs_info_conveniencia")
    private String obsInfoConveniencia;

  
    @Column(name="responsavel_id")
    private Long responsavelId;

  
    @Column(name="obs_responsavel")
    private String obsResponsavel;

  
    @Column(name="decisao_rh")
    private String decisaoRh;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="uuid")
    private UUID uuid;

  
    @Column(name="num_dia")
    private Integer numDia;

  
    @Column(name="ferias_gozadas_id")
    private Long feriasGozadasId;

  
    @Column(name="decisao_responsavel")
    private String decisaoResponsavel;

  
    @Column(name="obs_rh")
    private String obsRh;

  
}