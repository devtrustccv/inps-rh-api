/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_notificacao")
    @SequenceGenerator(name = "seq_notificacao", sequenceName = "SEQ_NOTIFICACAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotNull(message = "referenciaId is mandatory")
    @Column(name="referencia_id", nullable = false)
    private Long referenciaId;


  @Column(name="referencia_name")
    private String referenciaName;


  @Column(name="referencia_uuid")
    private UUID referenciaUuid;


  @Column(name="message")
    private String message;


  @Column(name="assunto")
    private String assunto;


  @Column(name="email")
    private String email;


  @Column(name="nome_receptor")
    private String nomeReceptor;


  @Column(name="data_envio")
    private LocalDate dataEnvio;


  @Column(name="url")
    private String url;


  @Column(name="estado")
    private String estado;


  @Column(name="uuid")
    private UUID uuid;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alerta_id", referencedColumnName = "id")
    private AlertaEntity alertaId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;
    @Column(name="tipo_notificacao")
    private String tipoNotificacao;


}
