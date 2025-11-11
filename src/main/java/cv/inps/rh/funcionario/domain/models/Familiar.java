package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class Familiar {


  private final Long id;
  private final IdentificadorUnico uuid;
  private TipoDocumento tipoDocumento;
  private String numDocumento;
  private String nome;
  private LocalDate dataNascimento;
  private String sexo;
  private String grauParentesco;
  private String dependencia;
  private String membroAgr;
  private String nmPai;
  private String nmMae;
  private Estado estado;

  private Familiar(Long id,
                   IdentificadorUnico uuid,
                   TipoDocumento tipoDocumento,
                   String numDocumento,
                   String nome,
                   LocalDate dataNascimento,
                   String sexo,
                   String grauParentesco,
                   String dependencia,
                   String membroAgr,
                   String nmPai,
                   String nmMae,
                   Estado estado) {
    this.id = id;
    this.uuid = uuid;
    this.tipoDocumento = tipoDocumento;
    this.numDocumento = numDocumento;
    this.nome = nome;
    this.dataNascimento = dataNascimento;
    this.sexo = sexo;
    this.grauParentesco = grauParentesco;
    this.dependencia = dependencia;
    this.membroAgr = membroAgr;
    this.nmPai = nmPai;
    this.nmMae = nmMae;
    this.estado = estado;
  }

  public static Familiar create(Long id,
                                TipoDocumento tipoDocumento,
                                String numDocumento,
                                String nome,
                                LocalDate dataNascimento,
                                String sexo,
                                String grauParentesco,
                                String dependencia,
                                String membroAgr,
                                String nmPai,
                                String nmMae) {
    return new Familiar(
        id != null && id > 0 ? id : null,
        IdentificadorUnico.create(),
        tipoDocumento,
        numDocumento,
        nome,
        dataNascimento,
        sexo,
        grauParentesco,
        dependencia,
        membroAgr,
        nmPai,
        nmMae,
        Estado.P
    );
  }

  public static Familiar rebuild(Long id,
                                 UUID uuid,
                                 TipoDocumento tipoDocumento,
                                 String numDocumento,
                                 String nome,
                                 LocalDate dataNascimento,
                                 String sexo,
                                 String grauParentesco,
                                 String dependencia,
                                 String membroAgr,
                                 String nmPai,
                                 String nmMae,
                                 Estado estado) {
    return new Familiar(
        id,
        IdentificadorUnico.from(uuid),
        tipoDocumento,
        numDocumento,
        nome,
        dataNascimento,
        sexo,
        grauParentesco,
        dependencia,
        membroAgr,
        nmPai,
        nmMae,
        estado
    );
  }

  /** Marca o familiar como eliminado (soft delete) */
  public void eliminar() {
    this.estado = Estado.E;
  }

  /** Atualiza os dados do familiar, apenas se forem não nulos */
  public void update(String nome,
                     LocalDate dataNascimento,
                     String sexo,
                     String grauParentesco,
                     String dependencia,
                     String membroAgr,
                     String nmPai,
                     String nmMae,
                     String numDocumento,
                     TipoDocumento tipoDocumento) {
    if (nome != null && !nome.isBlank()) this.nome = nome;
    if (dataNascimento != null) this.dataNascimento = dataNascimento;
    if (sexo != null && !sexo.isBlank()) this.sexo = sexo;
    if (grauParentesco != null && !grauParentesco.isBlank()) this.grauParentesco = grauParentesco;
    if (dependencia != null && !dependencia.isBlank()) this.dependencia = dependencia;
    if (membroAgr != null && !membroAgr.isBlank()) this.membroAgr = membroAgr;
    if (nmPai != null && !nmPai.isBlank()) this.nmPai = nmPai;
    if (nmMae != null && !nmMae.isBlank()) this.nmMae = nmMae;
    if (numDocumento != null && !numDocumento.isBlank()) this.numDocumento = numDocumento;
    if (tipoDocumento != null) this.tipoDocumento = tipoDocumento;
  }
}
