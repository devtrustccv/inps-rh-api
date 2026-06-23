package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "RH_T_ESTABELECIMENTO")
public class EstabelecimentoEntity extends AuditEntity {

  @Id
  @Column(name = "ID", nullable = false)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_estabelecimento")
  @SequenceGenerator(name = "seq_estabelecimento", sequenceName = "SEQ_ESTABELECIMENTO", allocationSize = 1)
  private Long id;

  @Size(max = 255)
  @NotNull
  @ColumnDefault("'A'")
  @Column(name = "ESTADO", nullable = false)
  private String estado;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PAIS_ID")
  private GeografiaEntity pais;

  @Size(max = 300)
  @Column(name = "NOME", length = 300)
  private String nome;

  @Size(max = 36)
  @Column(name = "UUID", length = 36)
  private String uuid;
}
