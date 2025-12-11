/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FERIAS")
public class FeriasEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ferias")
    @SequenceGenerator(name = "seq_ferias", sequenceName = "SEQ_FERIAS", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "funId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ano_id", referencedColumnName = "id")
    private AnoEntity anoId;
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="uuid")
    private UUID uuid;

  
}