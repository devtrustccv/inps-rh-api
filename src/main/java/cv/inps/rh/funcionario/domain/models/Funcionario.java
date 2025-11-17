package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
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
  private TipoDocumento tipoDocumento;
  private String numeroDocumento;
  private String nomeCompleto;
  private String fotografia;
  private LocalDate dataNascimento;
  private String sexo;
  private String nomeMae;
  private String nomePai;
  private String estadoCivil;
  private String nacionalidade;
  private Geografia localNascimento;
  private Long numeroFiscal; // NIF
  private String numeroSegurancaSocial; // INPS
  private final Long entidadeId;
  private final Long colaboradorId;
  private Estado estado;
  private Estado estadoValidacao;

  private List<Contacto> contactos;
  private Endereco endereco;
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
  private List<Validacao> validacoes;
  private List<OrdemServico> ordensServicos;
  private List<SituacaoLaboral> situacoesLaborais;

  private DocumentoPessoal documentoPessoal;

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
      Endereco endereco,
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
      List<DefPagamento> defPagamentos,
      List<Validacao> validacoes,
      List<OrdemServico> ordensServicos,
      List<SituacaoLaboral> situacoesLaborais,
      DocumentoPessoal documentoPessoal

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
    this.endereco = endereco;
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
    this.validacoes = validacoes != null ? validacoes : new ArrayList<>();
    this.ordensServicos = ordensServicos != null ? ordensServicos : new ArrayList<>();
    this.situacoesLaborais = situacoesLaborais != null ? situacoesLaborais : new ArrayList<>();
    this.documentoPessoal = documentoPessoal;

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
      Long colaboradorId,
      Endereco endereco
  ) {

    var documentoPessoal = DocumentoPessoal.create(numeroDocumento, tipoDocumento);

    var funcionario = new Funcionario(
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
        Estado.P,
        Estado.P,
        null,
        endereco,
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
        null,
        null,
        documentoPessoal
    );



    return funcionario;
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
      Endereco endereco,
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
      List<DefPagamento> defPagamentos,
      List<Validacao> validacoes,
      List<OrdemServico> ordensServicos,
      List<SituacaoLaboral> situacoesLaborais,
      DocumentoPessoal documentoPessoal

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
        endereco,
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
        defPagamentos,
        validacoes,
        ordensServicos,
        situacoesLaborais,
        documentoPessoal
    );
  }


  public void update(
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
      List<Contacto> contactos,
      Endereco endereco,
      List<Familiar> familiares,
      List<HabilitacaoLiteraria> habilitacoes,
      List<FormacaoFeita> formacoes,
      List<ExperienciaProfissional> experiencias,
      List<Documento> documentos,
      List<DadosBancarios> dadosBancarios
  ) {
    // campos simples
    if (tipoDocumento != null) this.tipoDocumento = tipoDocumento;
    if (numeroDocumento != null) this.numeroDocumento = numeroDocumento;
    if (nomeCompleto != null) this.nomeCompleto = nomeCompleto;
    if (fotografia != null) this.fotografia = fotografia;
    if (dataNascimento != null) this.dataNascimento = dataNascimento;
    if (sexo != null) this.sexo = sexo;
    if (nomeMae != null) this.nomeMae = nomeMae;
    if (nomePai != null) this.nomePai = nomePai;
    if (estadoCivil != null) this.estadoCivil = estadoCivil;
    if (nacionalidade != null) this.nacionalidade = nacionalidade;
    if (localNascimento != null) this.localNascimento = localNascimento;
    if (numeroFiscal != null) this.numeroFiscal = numeroFiscal;
    if (numeroSegurancaSocial != null) this.numeroSegurancaSocial = numeroSegurancaSocial;


    if (endereco != null) {
      this.endereco.update(
          endereco.getPais(),
          endereco.getIlha(),
          endereco.getConcelho(),
          endereco.getZona(),
          endereco.getMorada()
      );
    }

    // listas / relacionamentos
    syncContacts(contactos);
    syncFamiliares(familiares);
    syncHabilitacoes(habilitacoes);
    syncFormacoes(formacoes);
    syncExperiencias(experiencias);
    syncDocumentos(documentos);
    syncDadosBancarios(dadosBancarios);

  }

  public void validar(EstadoValidacao estadoValidacao) {

    if (estadoValidacao == null) return;
    Estado estado = null;

    if (estadoValidacao.equals(EstadoValidacao.SIM)) {
      estado = Estado.A;
      var tipoRelacionamento = getTipoRelacionamentoAtual();
      var validacao = getValidacao();

      var ordemServico = OrdemServico.create("Registo de colaborador " + nomeCompleto, "REGISTO_COLABORADOR",
          this.getId(), tipoRelacionamento.getContrato().getId(), tipoRelacionamento.getId(), validacao.getId());

      this.ordensServicos.add(ordemServico);
    } else {
      estado = Estado.I;

    }

    this.mudarEstado(estado);

  }

  private void mudarEstado(Estado estado) {

    this.estado = estado;
    this.estadoValidacao = estado;

    if (this.tiposRelacionamentos != null && !this.tiposRelacionamentos.isEmpty()) {
      this.tiposRelacionamentos.forEach(t -> t.mudarEstado(estado));
    }

    if (this.contratos != null && !this.contratos.isEmpty()) {
      this.contratos.forEach(c -> c.mudarEstado(estado));
    }

    if (this.mobilidades != null && !this.mobilidades.isEmpty()) {
      this.mobilidades.forEach(m -> m.mudarEstado(estado));
    }

    if (this.regimeTrabalhos != null && !this.regimeTrabalhos.isEmpty()) {
      this.regimeTrabalhos.forEach(rt -> rt.mudarEstado(estado));
    }

    if (this.defPagamentos != null && !this.defPagamentos.isEmpty()) {
      this.defPagamentos.forEach(dp -> dp.mudarEstado(estado));
    }

    if (this.definicaoRemuneracoes != null && !this.definicaoRemuneracoes.isEmpty()) {
      this.definicaoRemuneracoes.forEach(dr -> dr.mudarEstado(estado));
    }

    if (this.carreiras != null && !this.carreiras.isEmpty()) {
      this.carreiras.forEach(c -> c.mudarEstado(estado));
    }

    if (this.validacoes != null && !this.validacoes.isEmpty()) {
      mudarEstadoValidacaoDomain(estado);
    }
  }


  private Validacao getValidacao() {
    return this.validacoes.stream()
        .filter(v -> "REGISTO_COLABORADOR".equals(v.getReferenciaName()))
        .filter(v -> "INSERT".equals(v.getTipoAccao()))
        .findFirst().orElseThrow(() -> IgrpResponseStatusException.notFound("Validacao nao encontrada"));
  }


  private void mudarEstadoValidacaoDomain(Estado estado) {
      validacoes.stream()
          .filter(v -> "REGISTO_COLABORADOR".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.mudarEstado(estado));

  }

  public void adicionarValidacao(Validacao validacao) {
    if (validacao == null) return;
    this.validacoes.add(validacao);
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
          dados.getBanco(),
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

  /****** DefPagamentos *********************/
  public void syncDefPagamentos(List<DefPagamento> novosDefPagamentos) {
    if (novosDefPagamentos == null) return;

    // Adicionar ou atualizar
    for (DefPagamento novo : novosDefPagamentos) {
      addOrUpdateDefPagamento(novo);
    }

    for (DefPagamento existente : defPagamentos) {
      boolean aindaExiste = novosDefPagamentos.stream()
          .anyMatch(p -> Objects.equals(p.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.delete();
      }
    }
  }

  private void addOrUpdateDefPagamento(DefPagamento novo) {
    if (novo == null) return;

    Optional<DefPagamento> existenteOpt = findDefPagamentoById(novo.getId());
    if (existenteOpt.isPresent()) {
      DefPagamento existente = existenteOpt.get();
      existente.update(
          novo.getValor(),
          novo.getTipoMovimento(),
          novo.getDataInicio(),
          novo.getDataFim(),
          novo.getObs()
      );
    } else {
      defPagamentos.add(novo);
    }
  }

  private Optional<DefPagamento> findDefPagamentoById(Long id) {
    if (id == null) return Optional.empty();
    return this.defPagamentos.stream()
        .filter(p -> Objects.equals(p.getId(), id))
        .findFirst();
  }


  /****** DefinicaoRemuneracoes *********************/
  public void syncDefinicaoRemuneracoes(List<DefinicaoRemuneracao> novasDefinicoes) {
    if (novasDefinicoes == null) return;

    // Adicionar ou atualizar
    for (DefinicaoRemuneracao nova : novasDefinicoes) {
      addOrUpdateDefinicaoRemuneracao(nova);
    }

    // Soft delete das definições que não estão mais na nova lista
    for (DefinicaoRemuneracao existente : definicaoRemuneracoes) {
      boolean aindaExiste = novasDefinicoes.stream()
          .anyMatch(r -> Objects.equals(r.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateDefinicaoRemuneracao(DefinicaoRemuneracao nova) {
    if (nova == null) return;

    Optional<DefinicaoRemuneracao> existenteOpt = findDefinicaoRemuneracaoById(nova.getId());
    if (existenteOpt.isPresent()) {
      DefinicaoRemuneracao existente = existenteOpt.get();
      existente.update(
          nova.getPercentagem(),
          nova.getValor(),
          nova.getObs(),
          nova.getTipoMovimento()
      );
    } else {
      definicaoRemuneracoes.add(nova);
    }
  }

  private Optional<DefinicaoRemuneracao> findDefinicaoRemuneracaoById(Long id) {
    if (id == null) return Optional.empty();
    return this.definicaoRemuneracoes.stream()
        .filter(r -> Objects.equals(r.getId(), id))
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
                                        Geografia ilha,
                                        List<DefPagamento> pagamentos,
                                        List<DefinicaoRemuneracao> remuneracoes) {


    var contrato = Contrato.create(dataInicio, dataFim,
        duracaoMeses, null, "situacao laboral",
        paramVinculo, paramTipoContrato);

    var situacaoLaboral = SituacaoLaboral.create("situacaolab", "NOVO_CONTRATO",contrato , "obs");

    var carreira = Carreira.create(salario, null, "tipo situacao", "obs", contrato, paramCargo, paramEscalao, paramCategoria, paramCarreira);

    var mobilidade = Mobilidade.create(contrato, paramLocalTrab, "tipo siutacao", seccao, direcao, "obs",dataInicio, dataFim);

    var regime = RegimeTrabalho.create("tipo regime", "tipo situacao regime", dataFim, "obs", contrato);

    var tiposRelacionamento = TiposRelacionamento.create(paramCargo, direcao, paramVinculo, seccao, paramCategoria,
        paramEscalao, paramCarreira, salario, moeda, regimeTrabalho, null, null,
        dataInicio, dataFim, contrato, carreira, mobilidade, paramLocalTrab,
        regime, paramTipoContrato, null, null, "motivo", null,
        null, "obs","NOVO_CONTRATO");

    if (pagamentos != null && !pagamentos.isEmpty()) {
      pagamentos.forEach(p -> p.associate(contrato, tiposRelacionamento));
      this.syncDefPagamentos(pagamentos);
    }

    if (remuneracoes != null && !remuneracoes.isEmpty()) {
      remuneracoes.forEach(r -> r.associate(contrato));
      this.syncDefinicaoRemuneracoes(remuneracoes);
    }

    /*this.defPagamentos.addAll(pagamentos);
    this.definicaoRemuneracoes.addAll(remuneracoes);*/

    tiposRelacionamentos.add(tiposRelacionamento);
    contratos.add(contrato);
    carreiras.add(carreira);
    mobilidades.add(mobilidade);
    regimeTrabalhos.add(regime);
    situacoesLaborais.add(situacaoLaboral);

    var validacao = Validacao.create("INSERT", "REGISTO_COLABORADOR", null, "obs", tiposRelacionamento);
    this.adicionarValidacao(validacao);

  }


  public void atualizarDadosContratuais(ParamContrato paramTipoContrato,
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
                                        Geografia ilha,
                                        List<DefPagamento> pagamentos,
                                        List<DefinicaoRemuneracao> remuneracoes) {

    TiposRelacionamento tiposRelacionamentoAtual = getTipoRelacionamentoAtual();

    var contrato = getContratoById(tiposRelacionamentoAtual.getContrato().getId());
    contrato.update(dataInicio, dataFim, duracaoMeses, null, "situacao laboralLLLLLLLLLL",
        paramVinculo, paramTipoContrato);

    var carreira = getCarreiraById(tiposRelacionamentoAtual.getCarreira().getId());
    carreira.update(salario, null, "tipo situacaoadadas", "obs", contrato, paramCargo, paramEscalao, paramCategoria, paramCarreira);

    var mobilidade = getMobilidadeById(tiposRelacionamentoAtual.getMobilidade().getId());
    mobilidade.update(contrato, paramLocalTrab, "tipo siutacaojjjjjjjjj", seccao, direcao, "obs", dataInicio, dataFim);


    var regime = getRegimeById(tiposRelacionamentoAtual.getRegimeTrabalho().getId());
    regime.update("tipo regime", "tipo situacao regimerrrrrrrrrrrrr", dataFim, "obssssss", contrato);


    tiposRelacionamentoAtual.update(
        paramCargo,               // ParamCargo cargo
        direcao,                  // Instituicao instituicao
        paramVinculo,             // ParamVinculo vinculo
        seccao,                   // Secao seccao
        paramCategoria,           // ParamCategoria categoria
        paramEscalao,             // ParamEscalao escalao
        paramCarreira,            // ParamCarreira carrPcc
        salario,                  // BigDecimal salario
        moeda,                    // String moeda
        regimeTrabalho, // String regime (exemplo se RegimeTrabalho tiver getRegime)
        null,                     // String tipoSituacao
        null,                     // TiposRelacionamento tiprelAnterior
        null,                     // String flgProcessa
        "Atualizacao de contrato",// String obs
        dataInicio,               // LocalDate dataInicio
        dataFim,                  // LocalDate dataFim
        contrato.getDataInicio(), // LocalDate dataInicioContrato
        contrato.getDataFim(),    // LocalDate dataFimContrato
        contrato,                 // Contrato contrato
        carreira,                 // Carreira carreira
        mobilidade,               // Mobilidade mobilidade
        paramLocalTrab,           // ParamLocalTrab locTrab
        regime,                    // RegimeTrabalho regimeTrabalho
        paramTipoContrato,             // ParamContrato tipoContrato
        null,                     // String referente
        null,                     // LocalDate ultProc
        "Atualizacao motivada",   // String motivoSitLab
        null,                     // ParamSitLaboral situacLaboral
        null                      // String tpContrato
    );


    if (pagamentos != null && !pagamentos.isEmpty()) {
      pagamentos.forEach(p -> p.associate(contrato, tiposRelacionamentoAtual));
      this.syncDefPagamentos(pagamentos);
    }

    if (remuneracoes != null && !remuneracoes.isEmpty()) {
      remuneracoes.forEach(r -> r.associate(contrato));
      this.syncDefinicaoRemuneracoes(remuneracoes);
    }

  }

  public Contrato getContratoById(Long id) {
    return contratos.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst().orElseThrow(() -> IgrpResponseStatusException.notFound("contrato nao encontrado com id: " + id));
  }

  public Carreira getCarreiraById(Long id) {
    return carreiras.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst().orElseThrow(() -> IgrpResponseStatusException.notFound("carreira nao encontrado com id: " + id));
  }

  public Mobilidade getMobilidadeById(Long id) {
    return mobilidades.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst().orElseThrow(() -> IgrpResponseStatusException.notFound("mobilidade nao encontrado com id: " + id));
  }

  public RegimeTrabalho getRegimeById(Long id) {
    return regimeTrabalhos.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst().orElseThrow(() -> IgrpResponseStatusException.notFound("regime nao encontrado com id: " + id));
  }

  public TiposRelacionamento getTipoRelacionamentoAtual() {
    if (tiposRelacionamentos == null || tiposRelacionamentos.isEmpty()) {
      return null;
    }
    // 1. Filtra os com estadoActividadeAdm = 0
    List<TiposRelacionamento> ativos = tiposRelacionamentos.stream()
        .filter(t -> t.getEstadoActividadeAdm() != null && t.getEstadoActividadeAdm() == 0)
        .toList();

    // Caso existam estadoActividadeAdm = 0
    if (!ativos.isEmpty()) {
      return ativos.stream()
          .max(Comparator.comparing(TiposRelacionamento::getDataInicio))
          .orElse(null);
    }

    // 2. Não existiram 0 → procurar estadoActividadeAdm = 1
    List<TiposRelacionamento> inativos = tiposRelacionamentos.stream()
        .filter(t -> t.getEstadoActividadeAdm() != null && t.getEstadoActividadeAdm() == 1)
        .toList();

    if (!inativos.isEmpty()) {
      return inativos.stream()
          .max(Comparator.comparing(TiposRelacionamento::getDataFim))
          .orElse(null);
    }

    return null;
  }

  public void alterarSituacaoLaboral(String situacaoLaboral,
                                     String motivo,
                                     String observacao,
                                     Estado novoEstado) {

    if (this.estado.equals(novoEstado)) {
      throw IgrpResponseStatusException.badRequest("funcionario ja esta no estado: " + estado);
    }

    TiposRelacionamento atual = getTipoRelacionamentoAtual();
    if (atual == null) {
      throw IgrpResponseStatusException.badRequest("tiposRelacionamento atual nao encontrado");
    }
    atual.mudarEstadoActividadeAdm(0);


    var tiposRelacionamento = TiposRelacionamento.create(atual.getCargo(), atual.getInstituicao(), atual.getVinculo(), atual.getSeccao(),
        atual.getCategoria(), atual.getEscalao(), atual.getCarrPcc(), atual.getSalario(), atual.getMoeda(), atual.getRegime(), atual, null,
        atual.getDataInicio(), atual.getDataFim(), atual.getContrato(), atual.getCarreira(), atual.getMobilidade(), atual.getLocTrab(),
        atual.getRegimeTrabalho(), atual.getTipoContrato(), "SITUACAO_LABORAL", null, "motivo", null,
        null,"MUDANCA_SITUACAO_LAB", observacao);

    this.tiposRelacionamentos.add(tiposRelacionamento);

    var situacaoLaboralAtual = SituacaoLaboral.create(situacaoLaboral, motivo, atual.getContrato(), observacao);

    this.situacoesLaborais.add(situacaoLaboralAtual);

    var validacao = Validacao.create("UPDATE", "ESTADO_COLABORADOR", 1L, observacao, tiposRelacionamento);

    this.adicionarValidacao(validacao);

    this.mudarEstado(novoEstado);



  }



}
