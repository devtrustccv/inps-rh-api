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

import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_REMUN_TIPREL")
public class RemuneracaoTiprelEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_remun_tiprel")
    @SequenceGenerator(name = "seq_remun_tiprel", sequenceName = "SEQ_REMUN_TIPREL", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "estado is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private Estado estado;


    @Column(name="obs", length=4000)
    private String obs;


    @Column(name="uuid")
    private UUID uuid;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rem_id", referencedColumnName = "id")
    private DefinicaoRemuneracaoEntity remId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
}
