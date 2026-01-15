/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_VINCULO_MOV")
public class ParamVinculoMovimentoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_vinculo_movimento")
    @SequenceGenerator(name = "seq_vinculo_movimento", sequenceName = "SEQ_VINCULO_MOVIMENTO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "vinculoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vinculo_id", referencedColumnName = "id")
    private ParamVinculoEntity vinculoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tm_id", referencedColumnName = "id")
    private TipoMovimentoEntity tmId;
    @Column(name="uuid")
    private UUID uuid;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="tipo")
    private String tipo;

  
    @Column(name="percentagem")
    private Integer percentagem;

  
}