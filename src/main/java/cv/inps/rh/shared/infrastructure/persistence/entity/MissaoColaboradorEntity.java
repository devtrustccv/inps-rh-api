package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
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
@Table(name = "RH_T_MISSAO_COLABORADOR")
public class MissaoColaboradorEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_colaborador")
    @SequenceGenerator(name = "seq_missao_colaborador", sequenceName = "SEQ_MISSAO_COLABORADOR", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;

    // VARCHAR2 — o nº de documento pode ser alfanumérico (ex.: passaporte "PA466262")
    @Column(name = "num_documento", length = 255)
    private String numDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id")
    private MissaoServicoEntity missaoServId;

    @Column(name = "estado", length = 1)
    private String estado;

    @Column(name = "uuid", length = 100)
    private UUID uuid;
}
