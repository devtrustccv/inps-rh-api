/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.UUID;
import java.math.BigDecimal;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FALTA")
public class FaltaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_falta")
    @SequenceGenerator(name = "seq_falta", sequenceName = "SEQ_FALTA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "pedidoId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "sintese_diario_id", referencedColumnName = "id")
    private AssiduidadeSinteseDiarioEntity sinteseDiarioId;


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="descricao_motivo")
    private String descricaoMotivo;


    @Column(name="decisao_responsavel")
    private String decisaoResponsavel;


    @Column(name="obs_responsavel")
    private String obsResponsavel;


    @Column(name="despacho_rh")
    private String despachoRh;


    @Column(name="horas_ausencia")
    private String horasAusencia;


    @Column(name="data_inicio")
    private LocalDateTime dataInicio;


    @Column(name="data_fim")
    private LocalDateTime dataFim;


    @Column(name="flg_desconto_sal")
    private Integer flgDescontoSal;


    @Column(name="flg_justificativo")
    private String flgJustificativo;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="uuid")
    private UUID uuid;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "def_rem_id", referencedColumnName = "id")
    private DefinicaoRemuneracaoEntity defRemId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_sit_id", referencedColumnName = "id")
    private ParamSituacaoEntity paramSitId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", referencedColumnName = "id")
    private ResponsavelEntity responsavelId;
    @Column(name="valor")
    private BigDecimal valor;

}
