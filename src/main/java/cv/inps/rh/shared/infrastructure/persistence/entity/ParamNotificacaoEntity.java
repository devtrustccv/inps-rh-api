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
@Table(name = "RH_T_PARAM_NOTIFICACAO")
public class ParamNotificacaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_notif")
    @SequenceGenerator(name = "seq_param_notif", sequenceName = "SEQ_PARAM_NOTIF", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotBlank(message = "tipoNotificacao is mandatory")
    @Column(name="tipo_notificacao", nullable = false)
    private String tipoNotificacao;


  @Column(name="assunto")
    private String assunto;


  @Column(name="corpo")
    private String corpo;


  @Column(name="estado")
    private String estado;


  @Column(name="uuid")
    private UUID uuid;


}
