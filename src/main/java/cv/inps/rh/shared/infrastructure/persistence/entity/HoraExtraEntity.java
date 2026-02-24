/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_HORA_EXTRA")
public class HoraExtraEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_hora_extra")
    @SequenceGenerator(name = "seq_hora_extra", sequenceName = "SEQ_HORA_EXTRA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "tiprelId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sintese_diario_id", referencedColumnName = "id")
    private AssiduidadeSinteseDiarioEntity sinteseDiarioId;
    @Column(name="data_inicio")
    private LocalDate dataInicio;


    @Column(name="data_fim")
    private LocalDate dataFim;


    @Column(name="uuid")
    private UUID uuid;


    @Column(name="horas_diarias")
    private Long horasDiarias;


    @Column(name="valor_diario")
    private Integer valorDiario;


    @Column(name="percentagem")
    private Integer percentagem;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "def_rem_id", referencedColumnName = "id")
    private DefinicaoRemuneracaoEntity defRemId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;

    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


}
