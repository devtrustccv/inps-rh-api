/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
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
@Table(name = "RH_T_ALERTA")
public class AlertaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_alerta")
    @SequenceGenerator(name = "seq_alerta", sequenceName = "SEQ_ALERTA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name="referencia")
    private String referencia;

    @NotBlank(message = "referenciaName is mandatory")
    @Column(name="referencia_name", nullable = false)
    private String referenciaName;


    @Column(name="referencia_id")
    private Long referenciaId;


    @Column(name="referencia_uuid")
    private UUID referenciaUuid;


    @Column(name="descricao")
    private String descricao;


    @Column(name="estado")
    private String estado;


    @Column(name="tipo_situacao")
    private String tipoSituacao;


    @Column(name="tipo_alerta")
    private String tipoAlerta;


    @Column(name="flg_notificacao")
    private String flgNotificacao;


    @Column(name="flg_tratamento")
    private String flgTratamento;


    @Column(name="prioridade")
    private String prioridade;


    @Column(name="uuid")
    private UUID uuid;


}
