package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_XML_FOS")
public class RhXmlFosEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_XML_FOS_id_gen")
  @SequenceGenerator(name = "RH_XML_FOS_id_gen", sequenceName = "SEQ_RH_XML_FOS", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 10)
  @Column(name = "NU_CONTRIBUINTE", length = 10)
  private String nuContribuinte;

  @Size(max = 4)
  @Column(name = "ANO", length = 4)
  private String ano;

  @Size(max = 2)
  @Column(name = "MES", length = 2)
  private String mes;

  @Column(name = "DT_ENTREGA")
  private LocalDate dtEntrega;

  @Size(max = 20)
  @Column(name = "TT_REMUNERACAO", length = 20)
  private String ttRemuneracao;

  @Size(max = 20)
  @Column(name = "TT_CONTRIBUICAO", length = 20)
  private String ttContribuicao;

  @Size(max = 50)
  @Column(name = "TP_ENTREGA", length = 50)
  private String tpEntrega;

  @Size(max = 200)
  @Column(name = "OBS", length = 200)
  private String obs;

  @Lob
  @Column(name = "XML_GERADO")
  private String xmlGerado;

  @Size(max = 30)
  @Column(name = "NUM_DC", length = 30)
  private String numDc;

  @Column(name = "TT_CONTRIB_CALC")
  private Long ttContribCalc;
}
