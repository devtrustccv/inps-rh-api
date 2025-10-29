package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.Geografia;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.Instituicao;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

  private List<Contacto> contactos;
  private List<Endereco> enderecos;
  private List<Familiar> familiares;
  private List<HabilitacaoLiteraria> habilitacaoLiterarias;
  private List<FormacaoFeita> formacoes;
  private List<ExperienciaProfissional> experiencias;
  private List<Documento> documentos;
  private List<DadosBancarios> dadosBancarios;

  private List<TiposRelacionamento> tiposRelacionamentos;
  private List<Contrato> contratos;
  private List<Carreira> carreiras;
  private List<Mobilidade> mobilidades;
  private List<RegimeTrabalho> regimeTrabalhos;

  private List<DefinicaoRemuneracao> definicaoRemuneracoes;
  private List<DefPagamento> defPagamentos;


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
      List<HabilitacaoLiteraria> habilitacaoLiterarias,
      List<FormacaoFeita> formacoes,
      List<ExperienciaProfissional> experiencias,
      List<Documento> documentos,
      List<DadosBancarios> dadosBancarios,
      List<TiposRelacionamento> tiposRelacionamentos,
      List<Contrato> contratos,
      List<Carreira> carreiras,
      List<Mobilidade> mobilidades,
      List<RegimeTrabalho> regimeTrabalhos,
      List<DefinicaoRemuneracao> definicaoRemuneracoes,
      List<DefPagamento> defPagamentos

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
    this.contactos = contactos != null ? contactos : new ArrayList<>();
    this.enderecos = enderecos != null ? enderecos : new ArrayList<>();
    this.familiares = familiares != null ? familiares : new ArrayList<>();
    this.habilitacaoLiterarias = habilitacaoLiterarias != null ? habilitacaoLiterarias : new ArrayList<>();
    this.formacoes = formacoes != null ? formacoes : new ArrayList<>();
    this.experiencias = experiencias != null ? experiencias : new ArrayList<>();
    this.documentos = documentos != null ? documentos : new ArrayList<>();
    this.dadosBancarios = dadosBancarios != null ? dadosBancarios : new ArrayList<>();
    this.tiposRelacionamentos = tiposRelacionamentos != null ? tiposRelacionamentos : new ArrayList<>();
    this.contratos = contratos != null ? contratos : new ArrayList<>();
    this.carreiras = carreiras != null ? carreiras : new ArrayList<>();
    this.mobilidades = mobilidades != null ? mobilidades : new ArrayList<>();
    this.regimeTrabalhos = regimeTrabalhos != null ? regimeTrabalhos : new ArrayList<>();
    this.definicaoRemuneracoes = definicaoRemuneracoes != null ? definicaoRemuneracoes : new ArrayList<>();
    this.defPagamentos = defPagamentos != null ? defPagamentos : new ArrayList<>();

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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
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
      List<HabilitacaoLiteraria> habilitacaoLiterarias,
      List<FormacaoFeita> formacoes,
      List<ExperienciaProfissional> experiencias,
      List<Documento> documentos,
      List<DadosBancarios> dadosBancarios,
      List<TiposRelacionamento> tiposRelacionamentos,
      List<Contrato> contratos,
      List<Carreira> carreiras,
      List<Mobilidade> mobilidades,
      List<RegimeTrabalho> regimeTrabalhos,
      List<DefinicaoRemuneracao> definicaoRemuneracoes,
      List<DefPagamento> defPagamentos

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
        habilitacaoLiterarias,
        formacoes,
        experiencias,
        documentos,
        dadosBancarios,
        tiposRelacionamentos,
        contratos,
        carreiras,
        mobilidades,
        regimeTrabalhos,
        definicaoRemuneracoes,
        defPagamentos
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
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null

    );
  }


  public void addTipoRelacionamento(TiposRelacionamento tipoRelacionamento) {
    if (tiposRelacionamentos == null) tiposRelacionamentos = new ArrayList<>();
    tiposRelacionamentos.add(tipoRelacionamento);
  }

  public void addDadosBancarios(DadosBancarios dadosBancarios) {
    if (dadosBancarios == null) this.dadosBancarios = new ArrayList<>();
    this.dadosBancarios.add(dadosBancarios);
  }

  /****** contactos *********************/
  public void syncContacts(List<Contacto> newContacts) {
    if (newContacts == null) return;

    // Adicionar ou atualizar
    for (Contacto newContact : newContacts) {
      addOrUpdateContact(newContact);
    }

    // Soft delete dos contactos que não estão mais na nova lista
    for (Contacto existing : contactos) {
      boolean stillExists = newContacts.stream()
          .anyMatch(c -> Objects.equals(c.getId(), existing.getId()));
      if (!stillExists) {
        existing.eliminar();
      }
    }
  }

  private void addOrUpdateContact(Contacto contacto) {
    if (contacto == null) return;

    Optional<Contacto> existingOpt = findContactById(contacto.getId());
    if (existingOpt.isPresent()) {
      Contacto existing = existingOpt.get();
      existing.update(contacto.getTipoContacto(), contacto.getContacto());
    } else {
      this.contactos.add(contacto);
    }
  }

  private Optional<Contacto> findContactById(Long id) {
    if (id == null) return Optional.empty();
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
      if (familiares == null) familiares = new ArrayList<>();
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

  /****** Formações ********************/
  public void syncFormacoes(List<FormacaoFeita> novasFormacoes) {
    if (novasFormacoes == null) return;

    // Adicionar ou atualizar
    for (FormacaoFeita nova : novasFormacoes) {
      addOrUpdateFormacao(nova);
    }

    // Soft delete das formações que não estão mais na nova lista
    for (FormacaoFeita existente : formacoes) {
      boolean aindaExiste = novasFormacoes.stream()
          .anyMatch(f -> f.getId() != null && f.getId().equals(existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateFormacao(FormacaoFeita formacao) {
    if (formacao == null) return;

    var existenteOpt = formacoes.stream()
        .filter(f -> f.getId() != null && f.getId().equals(formacao.getId()))
        .findFirst();

    if (existenteOpt.isPresent()) {
      FormacaoFeita existente = existenteOpt.get();
      existente.update(
          formacao.getPais(),
          formacao.getEstabelecimento(),
          formacao.getTipoFormacao(),
          formacao.getCurso(),
          formacao.getNivel()
      );
    } else {
      formacoes.add(formacao);
    }
  }


  /****** Experiências Profissionais ********************/
  public void syncExperiencias(List<ExperienciaProfissional> novasExperiencias) {
    if (novasExperiencias == null) return;

    // Adicionar ou atualizar
    for (ExperienciaProfissional nova : novasExperiencias) {
      addOrUpdateExperiencia(nova);
    }

    // Soft delete das experiências que não estão mais na nova lista
    for (ExperienciaProfissional existente : experiencias) {
      boolean aindaExiste = novasExperiencias.stream()
          .anyMatch(e -> e.getId() != null && e.getId().equals(existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateExperiencia(ExperienciaProfissional experiencia) {
    if (experiencia == null) return;

    var existenteOpt = experiencias.stream()
        .filter(e -> e.getId() != null && e.getId().equals(experiencia.getId()))
        .findFirst();

    if (existenteOpt.isPresent()) {
      ExperienciaProfissional existente = existenteOpt.get();
      existente.update(
          experiencia.getPais(),
          experiencia.getEmpresa(),
          experiencia.getCargo(),
          experiencia.getDataInicio(),
          experiencia.getDataFim(),
          experiencia.getObservacao()
      );
    } else {
      experiencias.add(experiencia);
    }
  }

  /********* documentos *******/

  /****** documentos *********************/
  public void syncDocumentos(List<Documento> novosDocumentos) {
    if (novosDocumentos == null) return;

    // Adicionar ou atualizar
    for (Documento novo : novosDocumentos) {
      addOrUpdateDocumento(novo);
    }

    // Soft delete dos documentos que não estão mais na nova lista
    for (Documento existente : documentos) {
      boolean aindaExiste = novosDocumentos.stream()
          .anyMatch(d -> Objects.equals(d.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateDocumento(Documento documento) {
    if (documento == null) return;

    Optional<Documento> existenteOpt = findDocumentoById(documento.getId());
    if (existenteOpt.isPresent()) {
      Documento existente = existenteOpt.get();
      existente.update(
          documento.getTipoDocumento(),
          documento.getDocId(),
          documento.getReferenciaName(),
          documento.getReferenciaId()
      );
    } else {
      this.documentos.add(documento);
    }
  }

  private Optional<Documento> findDocumentoById(Long id) {
    if (id == null) return Optional.empty();
    return this.documentos.stream()
        .filter(d -> Objects.equals(d.getId(), id))
        .findFirst();
  }

  /****** DADOS BANCARIOS **************/

  public void syncDadosBancarios(List<DadosBancarios> novosDados) {
    if (novosDados == null) return;

    // Adicionar ou atualizar
    for (DadosBancarios novo : novosDados) {
      addOrUpdateDadosBancarios(novo);
    }

    // Soft delete dos dados bancários que não estão mais na nova lista
    for (DadosBancarios existente : dadosBancarios) {
      boolean aindaExiste = novosDados.stream()
          .anyMatch(d -> Objects.equals(d.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateDadosBancarios(DadosBancarios dados) {
    if (dados == null) return;

    Optional<DadosBancarios> existenteOpt = findDadosBancariosById(dados.getId());
    if (existenteOpt.isPresent()) {
      DadosBancarios existente = existenteOpt.get();
      existente.update(
          dados.getEntidade(),
          dados.getNumConta(),
          dados.getDataInicio(),
          dados.getDataFim(),
          dados.getObservacoes()
      );
    } else {
      this.dadosBancarios.add(dados);
    }
  }

  private Optional<DadosBancarios> findDadosBancariosById(Long id) {
    if (id == null) return Optional.empty();
    return this.dadosBancarios.stream()
        .filter(d -> Objects.equals(d.getId(), id))
        .findFirst();
  }


  public void adicionarDadosContratuais(ParamContrato paramTipoContrato,
                                        ParamCargo paramCargo, Instituicao direcao,
                                        Secao seccao, String centroCusto,
                                        ParamCarreira paramCarreira,
                                        ParamCategoria paramCategoria,
                                        ParamEscalao paramEscalao,
                                        ParamVinculo paramVinculo,
                                        String regimeTrabalho,
                                        BigDecimal salario,
                                        String moeda,
                                        LocalDate dataInicio,
                                        LocalDate dataFim,
                                        Integer duracaoMeses,
                                        ParamLocalTrab paramLocalTrab,
                                        Geografia pais,
                                        Geografia ilha) {


    var contrato = Contrato.create(dataInicio, dataFim,
        duracaoMeses, null, "situacao laboral",
        paramVinculo, paramTipoContrato);

    var carreira = Carreira.create(salario, null, "tipo situacao", "obs",contrato, paramCargo,paramEscalao, paramCategoria,paramCarreira);

    var mobilidade = Mobilidade.create(contrato, paramLocalTrab ,"tipo siutacao",seccao, direcao,"obs");

    var regime = RegimeTrabalho.create("tipo regime","tipo situacao regime",dataFim, "obs", contrato);

    var tiposRelacionamento = TiposRelacionamento.create(paramCargo, direcao, paramVinculo, seccao, paramCategoria,
        paramEscalao, paramCarreira, salario, moeda, regimeTrabalho, null, null,
        dataInicio, dataFim, contrato, carreira, mobilidade, paramLocalTrab,
        regime, paramTipoContrato, null, null, "motivo", null,
        null);



    tiposRelacionamentos.add(tiposRelacionamento);
    contratos.add(contrato);
    carreiras.add(carreira);
    mobilidades.add(mobilidade);
    regimeTrabalhos.add(regime);

  }
}
