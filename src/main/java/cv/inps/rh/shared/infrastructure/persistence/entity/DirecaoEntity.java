package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DIRECAO")
public class DirecaoEntity {

    // Só leitura: nem o RH nem os packages da BD criam direções, por isso não há gerador de id
    // (a SEQ_DIRECAO nunca existiu na BD e o ddl-auto=validate exigia-a).
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @NotBlank(message = "nome is mandatory")
    @Column(name = "NOME", nullable = false)
    private String nome;

    @NotBlank(message = "tipo is mandatory")
    @Column(name = "TIPO", nullable = false)
    private String tipo;

    @Column(name = "SIGA")
    private String siga;

    @Column(name = "DESCRICAO")
    private String descricao;

    @NotNull(message = "dtInicio is mandatory")
    @Column(name = "DT_INICIO", nullable = false)
    private LocalDate dtInicio;

    @Column(name = "DT_FIM")
    private LocalDate dtFim;

    @NotNull(message = "dtRegisto is mandatory")
    @Column(name = "DT_REGISTO", nullable = false, updatable = false)
    private LocalDateTime dtRegisto;

    @NotBlank(message = "estado is mandatory")
    @Column(name = "ESTADO", nullable = false, length = 1)
    private String estado;

    @NotNull(message = "idUser is mandatory")
    @Column(name = "ID_USER", nullable = false)
    private Long idUser;
}
