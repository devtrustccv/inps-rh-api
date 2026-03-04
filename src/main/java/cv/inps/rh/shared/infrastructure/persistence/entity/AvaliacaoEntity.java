package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "RH_T_AVD")
@Getter
@Setter
public class AvaliacaoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id")
  private FuncionarioEntity funId;

  @Column(name = "ano")
  private Integer ano;

  @Column(name = "avaliacao_final")
  private Double avaliacaoFinal;
}
