package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "RH_T_SUBSIDIO_NATAL")
public class SubsidioNatalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_SUBSIDIO_NATAL_id_gen")
    @SequenceGenerator(name = "RH_T_SUBSIDIO_NATAL_id_gen", sequenceName = "SEQ_SUBSIDIO_NATAL", allocationSize = 1)
    @Column(name = "ID", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "FUN_ID", nullable = false)
    private FuncionarioEntity fun;

    @NotNull
    @Column(name = "ANO_REFERENTE", nullable = false)
    private Long anoReferente;

    @NotNull
    @Column(name = "VALOR_SALARIO_BASE", nullable = false)
    private Long valorSalarioBase;

    @Size(max = 10)
    @Column(name = "MES_TRAB", length = 10)
    private String mesTrab;

    @Column(name = "PERC_SALARIO")
    private Long percSalario;

    @Column(name = "FALTAS")
    private Long faltas;

    @Column(name = "PERC_FALTA")
    private Long percFalta;

    @NotNull
    @Column(name = "VALOR_SUBSIDIO", nullable = false)
    private Long valorSubsidio;

    @Column(name = "CHEQUE_BRINDE")
    private Long chequeBrinde;

    @Column(name = "PRENDA_NATAL")
    private Long prendaNatal;

    @Size(max = 20)
    @NotNull
    @Column(name = "ESTADO", nullable = false, length = 20)
    private String estado;

    @Size(max = 100)
    @NotNull
    @Column(name = "UUID", nullable = false, length = 100)
    private String uuid;

    @Column(name = "PROC_SAL_ID")
    private Long procSalId;

    @NotNull
    @Column(name = "REFERENCIA_ID", nullable = false)
    private Long referenciaId;


}
