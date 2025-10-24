package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class HabilitacaoLiteraria {

  private final Long id;
  private final IdentificadorUnico uuid;
  private Geografia pais;
  private String estabelecimento;
  private String area;
  private String nomeCurso;
  private String nivel;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Boolean concluido;
  private Estado estado;

  private HabilitacaoLiteraria(
      Long id,
      IdentificadorUnico uuid,
      Geografia pais,
      String estabelecimento,
      String area,
      String nomeCurso,
      String nivel,
      LocalDate dataInicio,
      LocalDate dataFim,
      Boolean concluido,
      Estado estado
  ) {
    this.id = id;
    this.uuid = uuid;
    this.pais = pais;
    this.estabelecimento = estabelecimento;
    this.area = area;
    this.nomeCurso = nomeCurso;
    this.nivel = nivel;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.concluido = concluido;
    this.estado = estado;
  }

  // factory para criar nova habilitação
  public static HabilitacaoLiteraria create(
      Long id,
      Geografia pais,
      String estabelecimento,
      String area,
      String nomeCurso,
      String nivel,
      LocalDate dataInicio,
      LocalDate dataFim,
      Boolean concluido
  ) {
    return new HabilitacaoLiteraria(
        id != null && id > 0 ? id : null,
        IdentificadorUnico.create(),
        pais,
        estabelecimento,
        area,
        nomeCurso,
        nivel,
        dataInicio,
        dataFim,
        concluido,
        Estado.A
    );
  }

  // reconstrução para repositório
  public static HabilitacaoLiteraria rebuild(
      Long id,
      java.util.UUID uuid,
      Geografia pais,
      String estabelecimento,
      String area,
      String nomeCurso,
      String nivel,
      LocalDate dataInicio,
      LocalDate dataFim,
      Boolean concluido,
      Estado estado
  ) {
    return new HabilitacaoLiteraria(
        id,
        IdentificadorUnico.from(uuid),
        pais,
        estabelecimento,
        area,
        nomeCurso,
        nivel,
        dataInicio,
        dataFim,
        concluido,
        estado
    );
  }

  // soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  // update parcial
  public void update(
      Geografia pais,
      String estabelecimento,
      String area,
      String nomeCurso,
      String nivel,
      LocalDate dataInicio,
      LocalDate dataFim,
      Boolean concluido
  ) {
    if (pais != null) this.pais = pais;
    if (estabelecimento != null) this.estabelecimento = estabelecimento;
    if (area != null) this.area = area;
    if (nomeCurso != null) this.nomeCurso = nomeCurso;
    if (nivel != null) this.nivel = nivel;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (concluido != null) this.concluido = concluido;
  }

}
