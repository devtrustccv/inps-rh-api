/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "estado is mandatory")
    @Column(name="estado", nullable = false)
    private String estado;

  
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