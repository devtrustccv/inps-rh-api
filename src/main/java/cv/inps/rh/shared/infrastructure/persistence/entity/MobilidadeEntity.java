/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MOBILIDADE")
public class MobilidadeEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "tipoSituacao is mandatory")
    @Column(name="tipo_situacao", nullable = false)
    private String tipoSituacao;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="obs")
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;
    @Column(name="local_trab_id")
    private Long localTrabId;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secao_id", referencedColumnName = "id")
    private SecaoEntity secaoId;
    @Column(name="instid_id")
    private Long instidId;

  
}