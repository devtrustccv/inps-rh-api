package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;

@Getter
public class Funcionario {

  private final Long id;
  private final IdentificadorUnico uuid;
  private final TipoDocumento tipoDocumento;
  private final String numeroDocumento;
  private final String nomeCompleto;
  private final String fotografia;
  private final LocalDate dataNascimento;
  private final String sexo;
  private final String nomeMae;
  private final String nomePai;
  private final String estadoCivil;
  private final String nacionalidade;
  private final Geografia localNascimento;
  private final Long numeroFiscal; // NIF
  private final String numeroSegurancaSocial; // INPS
  private final Long entidadeId;
  private final Long colaboradorId;
  private final Estado estado;
  private final Estado estadoValidacao;

  private List<Contacto>  contactos;

  private Funcionario(
      Long id,
      IdentificadorUnico uuid,
      TipoDocumento tipoDocumento,
      String numeroDocumento,
      String nomeCompleto,
      String fotografia,
      LocalDate dataNascimento,
      String sexo,
      String nomeMae,
      String nomePai,
      String estadoCivil,
      String nacionalidade,
      Geografia localNascimento,
      Long numeroFiscal,
      String numeroSegurancaSocial,
      Long entidadeId,
      Long colaboradorId,
      Estado estado,
      Estado estadoValidacao,
      List<Contacto> contactos
  ) {
    this.id = id;
    this.uuid = uuid;
    this.tipoDocumento = tipoDocumento;
    this.numeroDocumento = numeroDocumento;
    this.nomeCompleto = nomeCompleto;
    this.fotografia = fotografia;
    this.dataNascimento = dataNascimento;
    this.sexo = sexo;
    this.nomeMae = nomeMae;
    this.nomePai = nomePai;
    this.estadoCivil = estadoCivil;
    this.nacionalidade = nacionalidade;
    this.localNascimento = localNascimento;
    this.numeroFiscal = numeroFiscal;
    this.numeroSegurancaSocial = numeroSegurancaSocial;
    this.entidadeId = entidadeId;
    this.colaboradorId = colaboradorId;
    this.estado = estado;
    this.estadoValidacao = estadoValidacao;
    this.contactos = contactos!=null? contactos : new ArrayList<>();
  }

  // factory metodo para criar um funcionario
  public static Funcionario create(
      TipoDocumento tipoDocumento,
      String numeroDocumento,
      String nomeCompleto,
      String fotografia,
      LocalDate dataNascimento,
      String sexo,
      String nomeMae,
      String nomePai,
      String estadoCivil,
      String nacionalidade,
      Geografia localNascimento,
      Long numeroFiscal,
      String numeroSegurancaSocial,
      Long entidadeId,
      Long colaboradorId
  ) {



    return new Funcionario(
        null,
        IdentificadorUnico.create(),
        tipoDocumento,
        numeroDocumento,
        nomeCompleto,
        fotografia,
        dataNascimento,
        sexo,
        nomeMae,
        nomePai,
        estadoCivil,
        nacionalidade,
        localNascimento,
        numeroFiscal,
        numeroSegurancaSocial,
        entidadeId,
        colaboradorId,
        Estado.A,
        Estado.P,
        null
    );
  }

  // Factory para reconstrução de repositorio
  public static Funcionario rebuild(
      Long id,
      UUID uuid,
      TipoDocumento tipoDocumento,
      String numeroDocumento,
      String nomeCompleto,
      String fotografia,
      LocalDate dataNascimento,
      String sexo,
      String nomeMae,
      String nomePai,
      String estadoCivil,
      String nacionalidade,
      Geografia localNascimento,
      Long numeroFiscal,
      String numeroSegurancaSocial,
      Long entidadeId,
      Long colaboradorId,
      Estado estado,
      Estado estadoValidacao,
      List<Contacto> contactos
  ) {
    return new Funcionario(
        id,
        IdentificadorUnico.from(uuid),
        tipoDocumento,
        numeroDocumento,
        nomeCompleto,
        fotografia,
        dataNascimento,
        sexo,
        nomeMae,
        nomePai,
        estadoCivil,
        nacionalidade,
        localNascimento,
        numeroFiscal,
        numeroSegurancaSocial,
        entidadeId,
        colaboradorId,
        estado,
        estadoValidacao,
        contactos
    );
  }


  public static Funcionario rebuildLight(
      Long id,
      UUID uuid,
      TipoDocumento tipoDocumento,
      String numeroDocumento,
      String nomeCompleto,
      String fotografia,
      LocalDate dataNascimento,
      String sexo,
      String nomeMae,
      String nomePai,
      String estadoCivil,
      String nacionalidade,
      Geografia localNascimento,
      Long numeroFiscal,
      String numeroSegurancaSocial,
      Long entidadeId,
      Long colaboradorId,
      Estado estado,
      Estado estadoValidacao
  ) {
    return new Funcionario(
        id,
        IdentificadorUnico.from(uuid),
        tipoDocumento,
        numeroDocumento,
        nomeCompleto,
        fotografia,
        dataNascimento,
        sexo,
        nomeMae,
        nomePai,
        estadoCivil,
        nacionalidade,
        localNascimento,
        numeroFiscal,
        numeroSegurancaSocial,
        entidadeId,
        colaboradorId,
        estado,
        estadoValidacao,
        null
    );
  }


  public void syncContacts(List<Contacto> newContacts) {
    if(newContacts == null) return;

    // Adicionar ou atualizar
    for(Contacto newContact : newContacts) {
      addOrUpdateContact(newContact);
    }

    // Soft delete dos contactos que não estão mais na nova lista
    for(Contacto existing : contactos) {
      boolean stillExists = newContacts.stream()
          .anyMatch(c -> Objects.equals(c.getId(), existing.getId()));
      if(!stillExists) {
        existing.eliminar();
      }
    }
  }

  private void addOrUpdateContact(Contacto contacto) {
    if(contacto == null) return;

    Optional<Contacto> existingOpt = findContactById(contacto.getId());
    if(existingOpt.isPresent()) {
      Contacto existing = existingOpt.get();
      existing.update(contacto.getTipoContacto(), contacto.getContacto());
    } else {
      this.contactos.add(contacto);
    }
  }

  private Optional<Contacto> findContactById(Long id) {
    if(id == null) return Optional.empty();
    return this.contactos.stream()
        .filter(c -> Objects.equals(c.getId(), id))
        .findFirst();
  }

}
