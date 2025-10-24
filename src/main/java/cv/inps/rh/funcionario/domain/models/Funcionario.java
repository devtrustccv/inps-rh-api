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
  private List<Endereco> enderecos;
  private List<Familiar> familiares;
  private List<HabilitacaoLiteraria> habilitacaoLiterarias;


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
      List<Contacto> contactos,
      List<Endereco> enderecos,
      List<Familiar> familiares,
      List<HabilitacaoLiteraria> habilitacaoLiterarias
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
    this.enderecos = enderecos != null ? enderecos : new ArrayList<>();
    this.familiares = familiares != null ? familiares : new ArrayList<>();
    this.habilitacaoLiterarias = habilitacaoLiterarias != null ? habilitacaoLiterarias : new ArrayList<>();

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
        null,
        null,
        null,
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
      List<Contacto> contactos,
      List<Endereco> enderecos,
      List<Familiar> familiares,
      List<HabilitacaoLiteraria> habilitacaoLiterarias
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
        contactos,
        enderecos,
        familiares,
        habilitacaoLiterarias
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
        null,
        null,
        null,
        null
    );
  }

 /****** contactos *********************/
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

  /****** enderecos *********************/
  public void syncEnderecos(List<Endereco> novosEnderecos) {
    if (novosEnderecos == null) return;

    // Adicionar ou atualizar
    for (Endereco novo : novosEnderecos) {
      addOrUpdateEndereco(novo);
    }

    // Soft delete dos endereços que não estão mais na nova lista
    for (Endereco existente : enderecos) {
      boolean aindaExiste = novosEnderecos.stream()
          .anyMatch(e -> Objects.equals(e.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateEndereco(Endereco novo) {
    if (novo == null) return;

    Optional<Endereco> existenteOpt = findEnderecoById(novo.getId());
    if (existenteOpt.isPresent()) {
      Endereco existente = existenteOpt.get();
      existente.update(
          novo.getPais(),
          novo.getIlha(),
          novo.getConcelho(),
          novo.getZona(),
          novo.getMorada()
      );
    } else {
      this.enderecos.add(novo);
    }
  }

  private Optional<Endereco> findEnderecoById(Long id) {
    if (id == null) return Optional.empty();
    return this.enderecos.stream()
        .filter(e -> Objects.equals(e.getId(), id))
        .findFirst();
  }

  /*************** familiares *********************/
  public void syncFamiliares(List<Familiar> novosFamiliares) {
    if (novosFamiliares == null) return;

    // Adicionar ou atualizar
    for (Familiar novo : novosFamiliares) {
      addOrUpdateFamiliar(novo);
    }

    // Soft delete dos familiares que não estão mais na nova lista
    for (Familiar existente : familiares) {
      boolean aindaExiste = novosFamiliares.stream()
          .anyMatch(f -> Objects.equals(f.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateFamiliar(Familiar familiar) {
    if (familiar == null) return;

    Optional<Familiar> existenteOpt = findFamiliarById(familiar.getId());
    if (existenteOpt.isPresent()) {
      Familiar existente = existenteOpt.get();
      existente.update(
          familiar.getNome(),
          familiar.getDataNascimento(),
          familiar.getSexo(),
          familiar.getGrauParentesco(),
          familiar.getDependencia(),
          familiar.getMembroAgr(),
          familiar.getNmPai(),
          familiar.getNmMae(),
          familiar.getNumDocumento(),
          familiar.getTipoDocumento()
      );
    } else {
      if(familiares == null) familiares = new ArrayList<>();
      this.familiares.add(familiar);
    }
  }

  private Optional<Familiar> findFamiliarById(Long id) {
    if (id == null) return Optional.empty();
    return this.familiares.stream()
        .filter(f -> Objects.equals(f.getId(), id))
        .findFirst();
  }

  /*************** habilitacoesLiterarias *********************/
  public void syncHabilitacoes(List<HabilitacaoLiteraria> novasHabilitacoes) {
    if (novasHabilitacoes == null) return;

    // Adicionar ou atualizar
    for (HabilitacaoLiteraria nova : novasHabilitacoes) {
      addOrUpdateHabilitacao(nova);
    }

    // Soft delete das habilitações que não estão mais na nova lista
    for (HabilitacaoLiteraria existente : habilitacaoLiterarias) {
      boolean aindaExiste = novasHabilitacoes.stream()
          .anyMatch(h -> Objects.equals(h.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateHabilitacao(HabilitacaoLiteraria habilitacao) {
    if (habilitacao == null) return;

    Optional<HabilitacaoLiteraria> existenteOpt = findHabilitacaoById(habilitacao.getId());
    if (existenteOpt.isPresent()) {
      HabilitacaoLiteraria existente = existenteOpt.get();
      existente.update(
          habilitacao.getPais(),
          habilitacao.getEstabelecimento(),
          habilitacao.getArea(),
          habilitacao.getNomeCurso(),
          habilitacao.getNivel(),
          habilitacao.getDataInicio(),
          habilitacao.getDataFim(),
          habilitacao.getConcluido()
      );
    } else {
      this.habilitacaoLiterarias.add(habilitacao);
    }
  }

  private Optional<HabilitacaoLiteraria> findHabilitacaoById(Long id) {
    if (id == null) return Optional.empty();
    return this.habilitacaoLiterarias.stream()
        .filter(h -> Objects.equals(h.getId(), id))
        .findFirst();
  }


}
