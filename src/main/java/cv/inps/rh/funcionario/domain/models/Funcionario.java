package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.parametrizacao.domain.models.TipoDocumento;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

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
      Estado estadoValidacao
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
        Estado.P
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
        estadoValidacao
    );
  }

}
