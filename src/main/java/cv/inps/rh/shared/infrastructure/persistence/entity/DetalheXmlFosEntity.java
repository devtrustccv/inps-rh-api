package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_DET_XML_FOS")
public class DetalheXmlFosEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_DET_XML_FOS_id_gen")
  @SequenceGenerator(name = "RH_DET_XML_FOS_id_gen", sequenceName = "SEQ_RH_DET_XML_FOS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 10)
  @Column(name = "NU_SEGURADO", length = 10)
  private String nuSegurado;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ID_FUNC")
  private FuncionarioEntity idFunc;

  @Size(max = 2)
  @Column(name = "NU_TRAB_AUTO", length = 2)
  private String nuTrabAuto;

  @Size(max = 2)
  @Column(name = "NU_TRAB_MAN", length = 2)
  private String nuTrabMan;

  @Column(name = "DT_REGISTO")
  private LocalDate dtRegisto;

  @Size(max = 20)
  @Column(name = "VL_REMUN_AUTO", length = 20)
  private String vlRemunAuto;

  @Size(max = 20)
  @Column(name = "VL_REMUN_MAN", length = 20)
  private String vlRemunMan;

  @Size(max = 5)
  @Column(name = "TIPO", length = 5)
  private String tipo;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.RESTRICT)
  @JoinColumn(name = "ID_XML_FOS")
  private XmlFosEntity idXmlFos;

  @Column(name = "ID_USER_UPDATE")
  private Long idUserUpdate;

  @Column(name = "DATA_UPDATE")
  private LocalDate dataUpdate;

  @Column(name = "DIR_SERV_ID")
  private Long dirServId;
}
