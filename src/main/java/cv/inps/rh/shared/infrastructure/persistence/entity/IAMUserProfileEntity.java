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
@Table(name = "t_iam_user_profile",
    // username e sub ja ficam indexados pelas constraints unique; so declaramos os restantes.
    // Nomes <= 30 chars por causa do limite de identificadores da Oracle legacy (xe).
    indexes = {
        @Index(name = "idx_iamup_full_name", columnList = "full_name"),
        @Index(name = "idx_iamup_user_id_old", columnList = "user_id_old")
    }
)
public class IAMUserProfileEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @NotBlank(message = "username is mandatory")
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "sub", unique = true)
    private String sub;

    @Column(name = "user_id_old", length = 255)
    private String userIdOld;
}
