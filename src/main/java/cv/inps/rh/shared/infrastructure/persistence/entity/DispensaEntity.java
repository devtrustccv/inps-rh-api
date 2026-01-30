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
@Table(name = "RH_T_DISPENSA")
public class DispensaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dispensa")
    @SequenceGenerator(name = "seq_dispensa", sequenceName = "SEQ_DISPENSA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tiprelId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;
    @Column(name="tipo_dispensa")
    private String tipoDispensa;

  
    @Column(name="descricao_motivo")
    private String descricaoMotivo;

  
    @Column(name="data")
    private LocalDate data;

  
    @Column(name="hora_inicio")
    private String horaInicio;

  
    @Column(name="hora_fim")
    private String horaFim;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="decisao_responsavel")
    private String decisaoResponsavel;

  
    @Column(name="obs_rh")
    private String obsRh;

  
    @Column(name="obs_responsavel")
    private String obsResponsavel;

  
    @Column(name="responsavel_id")
    private Long responsavelId;

  
    @Column(name="uuid")
    private UUID uuid;

  
}