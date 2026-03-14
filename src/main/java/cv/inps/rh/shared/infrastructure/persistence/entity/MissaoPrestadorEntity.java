package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "RH_T_MISSAO_PRESTADOR")
public class MissaoPrestadorEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_prestador")
    @SequenceGenerator(name = "seq_missao_prestador", sequenceName = "SEQ_MISSAO_PRESTADOR", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "entId is mandatory")
    @Column(name = "ent_id", nullable = false)
    private Long entId;

    @NotBlank(message = "nome is mandatory")
    @Column(name = "nome", length = 200, nullable = false)
    private String nome;

    @Email
    @NotBlank(message = "email is mandatory")
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @NotNull(message = "missaoServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoServicoEntity missaoServId;

    @NotNull(message = "estado is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 1, nullable = false)
    private Estado estado;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
