package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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

    @Column(name = "num_documento")
    private Long numDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id")
    private MissaoServicoEntity missaoServId;

    @Column(name = "estado", length = 1)
    private String estado;

    @Column(name = "uuid", length = 100)
    private UUID uuid;
}
