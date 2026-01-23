/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_TIPREL_REM_PAG")
public class TipoRelRemPagEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rh_s_tiprel_rem_pag")
    @SequenceGenerator(name = "rh_s_tiprel_rem_pag", sequenceName = "RH_S_TIPREL_REM_PAG", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tiprelId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rem_id", referencedColumnName = "id")
    private DefinicaoRemuneracaoEntity remId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pag_id", referencedColumnName = "id")
    private DefPagamentoEntity pagId;
}