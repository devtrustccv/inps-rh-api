/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.TipoConfig;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_CONFIG")
public class ParamConfigEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "tipoConfig is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name="tipo_config", nullable = false)
    private TipoConfig tipoConfig;


    @Column(name="referencia")
    private String referencia;


    @Column(name="descricao")
    private String descricao;


    @Column(name="flg_notificacao")
    private String flgNotificacao;


    @Column(name="flg_ordem_servico")
    private String flgOrdemServico;


    @Column(name="estado")
    private String estado;


}
