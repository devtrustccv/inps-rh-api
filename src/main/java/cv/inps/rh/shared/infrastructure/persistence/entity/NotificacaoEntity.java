/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_NOTIFICACAO")
public class NotificacaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "referencia is mandatory")
    @Column(name="referencia", nullable = false)
    private String referencia;

  
    @Column(name="message")
    private String message;

  
    @Column(name="assunto")
    private String assunto;

  
    @Column(name="email")
    private String email;

  
    @Column(name="nome_receptor")
    private String nOMERECEPTOR;

  
    @Column(name="data_envio")
    private LocalDate dATAENVIO;

  
    @Column(name="url")
    private String url;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="uuid")
    private UUID uuid;

  
}