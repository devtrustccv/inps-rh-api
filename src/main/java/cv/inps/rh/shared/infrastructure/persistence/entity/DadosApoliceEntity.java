package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_DADOS_APOLICE")
public class DadosApoliceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_DADOS_APOLICE_id_gen")
  @SequenceGenerator(
      name = "RH_T_DADOS_APOLICE_id_gen",
      sequenceName = "SEQ_DADOS_APOLICE",
      allocationSize = 1
  )
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 100)
  @NotNull
  @Column(name = "NUM_APOLICE", nullable = false, length = 100)
  private String numApolice;

  @NotNull
  @Column(name = "ILHA_ID", nullable = false)
  private Long ilhaId;

  @NotNull
  @Column(name = "DATA_APOLICE", nullable = false)
  private LocalDate dataApolice;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "DADOS_INSTITUICAO_ID", nullable = false)
  private DadosInstituicaoEntity dadosInstituicao;
}
