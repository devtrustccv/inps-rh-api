/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "RH_T_PARAM_LOCAL_TRAB")
public class ParamLocalTrabEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @Column(name="uuid")
    private UUID uuid;


  @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;


  @Column(name="nome_normalizado")
    private String nomeNormalizado;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_id", referencedColumnName = "id")
    private GeografiaEntity paisId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ilha_id", referencedColumnName = "id")
    private GeografiaEntity ilhaId;
    @Column(name="ups_id")
    private Long upsId;


  @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
