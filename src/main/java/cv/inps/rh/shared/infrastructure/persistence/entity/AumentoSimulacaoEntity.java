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
@Table(name = "RH_T_AUMENTO_SIMULACAO")
public class AumentoSimulacaoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_AUMENTO_SIMULACAO_id_gen")
  @SequenceGenerator(name = "RH_T_AUMENTO_SIMULACAO_id_gen", sequenceName = "SEQ_AUMENTO_SALARIAL_DET", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 20)
  @Column(name = "NIVEL_ESCALAO", length = 20)
  private String nivelEscalao;

  @Column(name = "SALARIO_ANTES")
  private Long salarioAntes;

  @Column(name = "SALARIO_DEPOIS")
  private Long salarioDepois;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;

  @Column(name = "INSTIT_ID")
  private Long institId;

  @Column(name = "SECAO_ID")
  private Long secaoId;

  @Column(name = "ESCALAO_ID")
  private Long escalaoId;

  @Column(name = "AUMENTO_SAL_ID")
  private Long aumentoSalId;
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "CARREIRA_ID", nullable = false)
  private CarreiraEntity carreira;
  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "FUN_ID")
  private FuncionarioEntity fun;


}
