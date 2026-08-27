package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_REMUNERACOES")
public class RhTRemuneracoe {
  @Id
  @Column(name = "ID", nullable = false)
  private Long id;

  @NotNull
  @Column(name = "VALOR", nullable = false)
  private Long valor;

  @NotNull
  @Column(name = "DATA_REF", nullable = false)
  private LocalDate dataRef;

  @Size(max = 3)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 3)
  private String estado;

  @Column(name = "IAC_ID")
  private Long iacId;

  @Size(max = 200)
  @Column(name = "IAC_DESCRICAO", length = 200)
  private String iacDescricao;

  @ColumnDefault("0")
  @Column(name = "VALOR_REAL")
  private Long valorReal;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @Column(name = "ID_UPLOAD")
  private Long idUpload;


}
