/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import cv.inps.rh.shared.domain.models.InstituicaoEntity;
import cv.inps.rh.shared.domain.models.FuncionarioEntity;
import cv.inps.rh.shared.domain.models.SecaoEntity;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_RESPONSAVEL")
public class ResponsavelEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_responsavel")
    @SequenceGenerator(name = "seq_responsavel", sequenceName = "SEQ_RESPONSAVEL", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "institId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instit_id", referencedColumnName = "id")
    private InstituicaoEntity institId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;
    @Column(name="email")
    private String email;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secao_id", referencedColumnName = "id")
    private SecaoEntity secaoId;
}