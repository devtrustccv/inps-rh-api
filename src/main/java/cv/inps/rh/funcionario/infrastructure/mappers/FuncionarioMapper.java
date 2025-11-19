package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.domain.filters.FuncionarioFilter;
import cv.inps.rh.funcionario.domain.models.*;
import cv.inps.rh.funcionario.domain.projections.FuncionarioList;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.mappers.EstadoMapper;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EstadoMapper estadoMapper;
  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final TiposRelacionamentoMapper tiposRelacionamentoMapper;
  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final ValidacaoMapper validacaoMapper;
  private final OrdemServicoMapper ordemServicoMapper;
  private final DocumentoPessoalMapper documentoPessoalMapper;
  private final SituacaoLaboralMapper situacaoLaboralMapper;
  private final DadosContratuaisMapper contratuaisEntityMapper;


  private final EntityManager entityManager;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  /**
   * Converts JPA entity to domain Funcionario
   */
  public Funcionario toDomain(FuncionarioEntity entity) {
    if (entity == null) return null;

    List<Contacto> contactos = entity.getContactos() != null
        ? entity.getContactos().stream()
        .map(contactoMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    Endereco endereco = entity.getEndereco() != null
        ? enderecoMapper.toDomain(entity.getEndereco())
        : null;

    DocumentoPessoal documentoPessoal = entity.getDocumentoPessoal() != null
        ? documentoPessoalMapper.toDomain(entity.getDocumentoPessoal())
        : null;

    List<Familiar> familiares = entity.getFamiliares() != null
        ? entity.getFamiliares().stream().map(familiarMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<HabilitacaoLiteraria> habilitacoesLiterarias = entity.getHabilitacoesLiterarias() != null
        ? entity.getHabilitacoesLiterarias().stream().map(habilitacaoLiterariaMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<FormacaoFeita> formacaoFeitas = entity.getFormacoesFeitas() != null ?
        entity.getFormacoesFeitas().stream().map(formacaoFeitaMapper::toDomain)
            .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<ExperienciaProfissional> experienciasProfissionais = entity.getExperienciasProfissionais() != null ?
        entity.getExperienciasProfissionais().stream().map(experienciaProfissionalMapper::toDomain)
            .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<Documento> documentos = entity.getDocumentos() != null ? entity.getDocumentos().stream()
        .map(documentoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<DadosBancarios> dadosBancarios = entity.getDadosBancarios() != null ? entity.getDadosBancarios().stream()
        .map(dadosBancariosMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<TiposRelacionamento> tiposRelacionamentos = entity.getTiposrelacionamentos() != null ? entity.getTiposrelacionamentos().stream()
        .map(tiposRelacionamentoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Contrato> contratos = entity.getContratos() != null ? entity.getContratos().stream()
        .map(contratoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Carreira> carreiras = entity.getCarreiras() != null ? entity.getCarreiras().stream()
        .map(carreiraMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Mobilidade> mobilidades = entity.getMobilidades() != null ? entity.getMobilidades().stream()
        .map(mobilidadeMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<RegimeTrabalho> regimeTrabalhos = entity.getRegimesTrabalhos() != null ? entity.getRegimesTrabalhos().stream()
        .map(regimeTrabalhoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<DefinicaoRemuneracao> definicaoRemuneracoes = entity.getDefinicoesRenumeracoes() != null ? entity.getDefinicoesRenumeracoes()
        .stream().map(definicaoRemuneracaoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<DefPagamento> defPagamentos = entity.getDefinicoesPagamentos() != null ? entity.getDefinicoesPagamentos()
        .stream().map(defPagamentoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Validacao> validacoes = entity.getValidacoes() != null ? entity.getValidacoes()
        .stream().map(validacaoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    /*List<OrdemServicoEntity> = entity.getOrdemServicos()!=null ? entity.getValidacoes()
        .stream().map(ordemServicoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();*/

    List<SituacaoLaboral> situacaoLaborais = entity.getSituacoesLaborais() != null ? entity.getSituacoesLaborais()
        .stream().map(situacaoLaboralMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    return Funcionario.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoDocumentoId() != null ? tipoDocumentoMapper.toDomain(entity.getTipoDocumentoId()) : null,
        entity.getNumDocumento(),
        entity.getNome(),
        entity.getFotografia(),
        entity.getDataNascimento(),
        entity.getSexo(),
        entity.getNmMae(),
        entity.getNmPai(),
        entity.getEstadoCivil(),
        entity.getNacionalidade(),
        entity.getLocNascId() != null ? geografiaMapper.toDomain(entity.getLocNascId()) : null,
        entity.getNif(),
        entity.getNuSegInps(),
        entity.getEntId(),
        entity.getIdColaborador(),
        entity.getEstado(),
        estadoMapper.fromString(entity.getEstadoValidacao()),
        contactos,
        endereco,
        familiares,
        habilitacoesLiterarias,
        formacaoFeitas,
        experienciasProfissionais,
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
        null,
        situacaoLaborais,
        documentoPessoal
    );
  }


  /**
   * Converts domain Funcionario to JPA entity
   */
  public FuncionarioEntity toEntity(Funcionario funcionario) {
    if (funcionario == null) return null;

    FuncionarioEntity entity = new FuncionarioEntity();
    ;
    entity.setId(funcionario.getId() != null && funcionario.getId() > 0 ? funcionario.getId() : null);

    System.out.println("entity id 1_:::::::::::::::::::::::::::::" + entity.getId());
    System.out.println("entity id _:::::::::::::::::::::::::::::" + entity.getId());
    entity.setUuid(funcionario.getUuid().getValor());
    entity.setTipoDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, funcionario.getTipoDocumento().getId()));
    entity.setNumDocumento(funcionario.getNumeroDocumento());
    entity.setNome(funcionario.getNomeCompleto());
    entity.setFotografia(funcionario.getFotografia());
    entity.setDataNascimento(funcionario.getDataNascimento());
    entity.setSexo(funcionario.getSexo());
    entity.setNmMae(funcionario.getNomeMae());
    entity.setNmPai(funcionario.getNomePai());
    entity.setEstadoCivil(funcionario.getEstadoCivil());
    entity.setNacionalidade(funcionario.getNacionalidade());
    entity.setLocNascId(entityManager.getReference(GeografiaEntity.class, funcionario.getLocalNascimento().getId()));
    entity.setNif(funcionario.getNumeroFiscal());
    entity.setNuSegInps(funcionario.getNumeroSegurancaSocial());
    entity.setEntId(funcionario.getEntidadeId());
    entity.setIdColaborador(funcionario.getColaboradorId());
    entity.setEstado(funcionario.getEstado());
    entity.setEstadoValidacao(funcionario.getEstadoValidacao().name());


    //contactos
    if (funcionario.getContactos() != null) {
      var contactosEntities = funcionario.getContactos().stream()
          .map(contactoMapper::toEntity)
          .collect(Collectors.toList());

      contactosEntities.forEach(c -> c.setFunId(entity)); // garante o relacionamento
      entity.setContactos(contactosEntities);
    }

    //endereco
    if (funcionario.getEndereco() != null) {
      var enderecoEntity = enderecoMapper.toEntity(funcionario.getEndereco());
      enderecoEntity.setFunId(entity);
      entity.setEndereco(enderecoEntity);
    }

    //documento pessoal
    if (funcionario.getDocumentoPessoal() != null) {
      var documentoPessoalEntity = documentoPessoalMapper.toEntity(funcionario.getDocumentoPessoal());
      documentoPessoalEntity.setFunId(entity);
      entity.setDocumentoPessoal(documentoPessoalEntity);
    }

    // familiares
    if (funcionario.getFamiliares() != null) {
      var familiaresEntities = funcionario.getFamiliares().stream()
          .map(familiarMapper::toEntity)
          .collect(Collectors.toList());

      familiaresEntities.forEach(f -> f.setFunId(entity));
      entity.setFamiliares(familiaresEntities);
    }

    // habilitacoes literarias
    if (funcionario.getHabilitacaoLiterarias() != null) {
      var habilitacoesLiterariasEntities = funcionario.getHabilitacaoLiterarias().stream()
          .map(habilitacaoLiterariaMapper::toEntity)
          .collect(Collectors.toList());

      habilitacoesLiterariasEntities.forEach(h -> h.setFunId(entity));
      entity.setHabilitacoesLiterarias(habilitacoesLiterariasEntities);
    }

    // formacoes feitas
    if (funcionario.getFormacoes() != null) {
      var formacoesEntities = funcionario.getFormacoes().stream()
          .map(formacaoFeitaMapper::toEntity)
          .collect(Collectors.toList());

      formacoesEntities.forEach(f -> f.setFunId(entity));
      entity.setFormacoesFeitas(formacoesEntities);
    }

    // experiencias profissionais
    if (funcionario.getExperiencias() != null) {
      var experienciasEntities = funcionario.getExperiencias().stream()
          .map(experienciaProfissionalMapper::toEntity)
          .collect(Collectors.toList());
      experienciasEntities.forEach(e -> e.setFunId(entity));
      entity.setExperienciasProfissionais(experienciasEntities);
    }

    //documentos
    if (funcionario.getDocumentos() != null) {
      var documentosEntities = funcionario.getDocumentos().stream()
          .map(documentoMapper::toEntity)
          .collect(Collectors.toList());
      documentosEntities.forEach(d -> d.setFunId(entity));
      entity.setDocumentos(documentosEntities);
    }

    //dados bancarios
    if (funcionario.getDadosBancarios() != null) {
      var dadosBancariosEntities = funcionario.getDadosBancarios().stream()
          .map(dadosBancariosMapper::toEntity)
          .collect(Collectors.toList());
      dadosBancariosEntities.forEach(d -> d.setFunId(entity));
      entity.setDadosBancarios(dadosBancariosEntities);
    }

    // contratos
    Map<UUID, ContratoEntity> contratosMap = new HashMap<>();
    if (funcionario.getContratos() != null) {
      List<ContratoEntity> contratosEntities = funcionario.getContratos().stream()
          .map(c -> {
            ContratoEntity ce = contratoMapper.toEntity(c);
            ce.setFunId(entity);
            System.out.println("ce.getFunId().getId() = " + ce.getFunId().getId());
            contratosMap.put(c.getUuid().getValor(), ce);
            return ce;
          })
          .collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setContratos(contratosEntities);
    }

    // carreiras
    Map<UUID, CarreiraEntity> carreirasMap = new HashMap<>();
    if (funcionario.getCarreiras() != null) {
      var carreirasEntities = funcionario.getCarreiras().stream()
          .map(c -> {
            CarreiraEntity ce = carreiraMapper.toEntity(c);
            ce.setFunId(entity);
            carreirasMap.put(c.getUuid().getValor(), ce);
            return ce;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setCarreiras(carreirasEntities);
    }

    //mobilidades
    Map<UUID, MobilidadeEntity> modilidadesMap = new HashMap<>();
    if (funcionario.getMobilidades() != null) {
      var mobilidadesEntities = funcionario.getMobilidades().stream()
          .map(m -> {
            MobilidadeEntity ce = mobilidadeMapper.toEntity(m);
            ce.setFunId(entity);
            modilidadesMap.put(m.getUuid().getValor(), ce);
            return ce;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setMobilidades(mobilidadesEntities);
    }


    //regimes trabalhos
    Map<UUID, RegimeTrabalhoEntity> regimesMap = new HashMap<>();
    if (funcionario.getRegimeTrabalhos() != null) {
      var regimeTrabalhosEntities = funcionario.getRegimeTrabalhos().stream()
          .map(rt -> {
            RegimeTrabalhoEntity entityRt = regimeTrabalhoMapper.toEntity(rt);
            entityRt.setFunId(entity);
            regimesMap.put(rt.getUuid().getValor(), entityRt);
            return entityRt;
          }).collect(Collectors.toCollection(ArrayList::new));

      entity.setRegimesTrabalhos(regimeTrabalhosEntities);
    }


    //tipos relacionamentos
    Map<UUID, TiposRelacionamentoEntity> tiposRelacionamentosMap = new HashMap<>();
    if (funcionario.getTiposRelacionamentos() != null) {
      List<TiposRelacionamentoEntity> tiposEntities = funcionario.getTiposRelacionamentos().stream()
          .map(t -> {
            TiposRelacionamentoEntity tre = tiposRelacionamentoMapper.toEntity(t);
            tre.setFunId(entity);

            // pega as referências do mesmo mapa
            tre.setContratoId(contratosMap.get(t.getContrato().getUuid().getValor()));
            tre.setCarreiraId(carreirasMap.get(t.getCarreira().getUuid().getValor()));
            tre.setMobId(modilidadesMap.get(t.getMobilidade().getUuid().getValor()));
            tre.setRegimeId(regimesMap.get(t.getRegimeTrabalho().getUuid().getValor()));

            tiposRelacionamentosMap.put(t.getUuid().getValor(), tre);
            return tre;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setTiposrelacionamentos(tiposEntities);
    }


    //definicoes remuneracoes
    if (funcionario.getDefinicaoRemuneracoes() != null) {
      List<DefinicaoRemuneracaoEntity> definicaoRemuneracaoEntities = funcionario.getDefinicaoRemuneracoes().stream()
          .map(d -> {
            // Converte para entity usando o mapper
            DefinicaoRemuneracaoEntity dre = definicaoRemuneracaoMapper.toEntity(d);
            // Associa o funcionário
            dre.setFunId(entity);
            return dre;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;

      entity.setDefinicoesRenumeracoes(definicaoRemuneracaoEntities);
    }

    // Definição pagamento
    if (funcionario.getDefPagamentos() != null) {
      List<DefPagamentoEntity> defPagamentosEntities = funcionario.getDefPagamentos().stream()
          .map(d -> {
            DefPagamentoEntity dpe = defPagamentoMapper.toEntity(d);
            dpe.setFunId(entity);
            return dpe;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setDefinicoesPagamentos(defPagamentosEntities);
    }


    //validacoes
    if (funcionario.getValidacoes() != null) {
      List<ValidacaoEntity> validacaoEntities = funcionario.getValidacoes().stream()
          .map(v -> {
            ValidacaoEntity validacaoEntity = validacaoMapper.toEntity(v);
            validacaoEntity.setReferenciaId(1L);
            validacaoEntity.setFunId(entity);
            validacaoEntity.setTiprelId(tiposRelacionamentosMap.get(v.getTiprel().getUuid().getValor()));

            return validacaoEntity;
          }).collect(Collectors.toCollection(ArrayList::new));
      ;
      entity.setValidacoes(validacaoEntities);
    }

    //ordem servico
    if (funcionario.getOrdensServicos() != null && !funcionario.getOrdensServicos().isEmpty()) {
      List<OrdemServicoEntity> ordemServicoEntities = funcionario.getOrdensServicos().stream()
          .map(ordemServicoMapper::toEntity).collect(Collectors.toCollection(ArrayList::new));
      entity.setOrdemServicos(ordemServicoEntities);
    }


    // situacao laboral
    if (funcionario.getSituacoesLaborais() != null) {
      List<SituacaoLaboralEntity> situacaoLaboralEntities = funcionario.getSituacoesLaborais().stream()
          .map(s -> {
            SituacaoLaboralEntity sle = situacaoLaboralMapper.toEntity(s);
            // Associa o funcionário
            sle.setFunId(entity);
            // Associa o contrato correto
            return sle;
          })
          .collect(Collectors.toCollection(ArrayList::new));

      entity.setSituacoesLaborais(situacaoLaboralEntities);
    }


    return entity;
  }


  public FuncionarioFilter toFilterDomain(String nome,
                                          Long direcao,
                                          Long seccao,
                                          Long tipoVinculoLaboral,
                                          String dataInicio,
                                          String dataFim,
                                          String estado,
                                          Integer pageNumber,
                                          Integer pageSize) {

    return FuncionarioFilter.builder()
        .nome(nome)
        .direcao(direcao)
        .seccao(seccao)
        .tipoVinculoLaboral(tipoVinculoLaboral)
        .dataInicio(StringUtils.hasText(dataInicio) ? DateFormatter.stringToLocalDateTime(dataInicio) : null)
        .dataFim(StringUtils.hasText(dataFim) ? DateFormatter.stringToLocalDateTime(dataFim) : null)
        .estado(estado != null ? Estado.fromCodeOrThrow(estado) : null)
        .pageNumber(pageNumber)
        .pageSize(pageSize)
        .build();
  }

  public FuncionarioListDTO toDTO(FuncionarioList projection) {
    if (projection == null) return null;

    FuncionarioListDTO dto = new FuncionarioListDTO();
    dto.setId(projection.getId());
    dto.setUuid(projection.getUuid() != null ? projection.getUuid().toString() : null);
    dto.setNome(projection.getNome());
    dto.setCargo(projection.getCargo());
    dto.setDataInicio(projection.getDataInicio() != null ? DateFormatter.localDateToString(projection.getDataInicio()) : null);
    dto.setDireccao(projection.getDireccao());
    dto.setSeccao(projection.getSeccao());
    dto.setCarreiraCategoria(projection.getCarreiraCategoria());
    dto.setEstadoRegisto(projection.getEstadoRegisto());
    dto.setEstadoColaborador(projection.getEstadoColaborador());

    return dto;
  }


  public FuncionarioResponseDTO toResponseDTO(Funcionario funcionario) {
    if (funcionario == null) return null;

    var dto = new FuncionarioResponseDTO();

    // ---- Dados Pessoais ----
    DadosPessoaisRespDTO dadosPessoais = new DadosPessoaisRespDTO();
    dadosPessoais.setId(funcionario.getId());
    dadosPessoais.setUuid(funcionario.getUuid() != null ? funcionario.getUuid().toString() : null);
    dadosPessoais.setNome(funcionario.getNomeCompleto());
    dadosPessoais.setDataNascimento(funcionario.getDataNascimento());
    dadosPessoais.setGenero(funcionario.getSexo());
    dadosPessoais.setNomeMae(funcionario.getNomeMae());
    dadosPessoais.setNomePai(funcionario.getNomePai());
    dadosPessoais.setEstadoCivil(funcionario.getEstadoCivil());
    dadosPessoais.setNacionalidade(funcionario.getNacionalidade());
    dadosPessoais.setTipoDocumentoId(funcionario.getTipoDocumento().getId());
    dadosPessoais.setNumDocumento(funcionario.getNumeroDocumento());
    dadosPessoais.setNif(funcionario.getNumeroFiscal() != null ? funcionario.getNumeroFiscal() : null);
    dadosPessoais.setNumSegurado(funcionario.getNumeroSegurancaSocial());
    dadosPessoais.setUrlFoto(funcionario.getFotografia());
    dadosPessoais.setNaturalidadeId(funcionario.getLocalNascimento().getId());
    dadosPessoais.setNaturalidadeDesc(funcionario.getLocalNascimento().getNome());
    dadosPessoais.setEndereco(enderecoMapper.toDTO(funcionario.getEndereco()));
    dadosPessoais.setContactos(contactoMapper.toDTOList(funcionario.getContactos()));

    dto.setDadosPessoais(dadosPessoais);

    // ---- Familiares ----
    if (funcionario.getFamiliares() != null && !funcionario.getFamiliares().isEmpty()) {
      dto.setFamiliares(familiarMapper.toResponseDTOList(funcionario.getFamiliares()));
    }

    // ---- Dados Acadêmicos e Profissionais ----
    DadosAcademicosProfResponseDTO dadosAcademicosProf = new DadosAcademicosProfResponseDTO();
    dadosAcademicosProf.setHabilitacoesLiterarias(habilitacaoLiterariaMapper.toResponseDTOList(funcionario.getHabilitacaoLiterarias()));
    dadosAcademicosProf.setFormacoesFeitas(formacaoFeitaMapper.toResponseDTOList(funcionario.getFormacoes()));
    dadosAcademicosProf.setExperienciasProfssionais(experienciaProfissionalMapper.toResponseDTOList(funcionario.getExperiencias()));
    dto.setDadosAcademicosProf(dadosAcademicosProf);


    // ---- Dados Bancários ----
    if (funcionario.getDadosBancarios() != null && !funcionario.getDadosBancarios().isEmpty()) {
      dto.setDadosBancarios(dadosBancariosMapper.toResponseDTOList(funcionario.getDadosBancarios()));
    }

    // ---- Anexos / Documentos ----
    if (funcionario.getDocumentos() != null && !funcionario.getDocumentos().isEmpty()) {
      dto.setAnexos(documentoMapper.toResponseDTOList(funcionario.getDocumentos()));
    }

    // ---- Dados Contratuais ----
    if (funcionario.getTipoRelacionamentoAtual() != null) {
      dto.setDadosContratuais(this.dadosContratuaisRespDTO(funcionario));
    }

    return dto;
  }


  public DadosContratuaisRespDTO dadosContratuaisRespDTO(Funcionario funcionario) {
    if (funcionario.getTipoRelacionamentoAtual() == null) return null;

    var tipoRelacionamentoAtual = funcionario.getTipoRelacionamentoAtual();

    var dadosContratuaisRespDTO = new DadosContratuaisRespDTO();
    dadosContratuaisRespDTO.setTipoContratoId(tipoRelacionamentoAtual.getContrato().getTpContratoParam().getId());
    dadosContratuaisRespDTO.setTipoContratoDesc(tipoRelacionamentoAtual.getContrato().getTpContratoParam().getNome());
    dadosContratuaisRespDTO.setCargoPosicaoId(tipoRelacionamentoAtual.getCargo().getId());
    dadosContratuaisRespDTO.setCargoPosicaoDesc(tipoRelacionamentoAtual.getCargo().getNome());
    dadosContratuaisRespDTO.setDirecaoId(tipoRelacionamentoAtual.getInstituicao().getId());
    dadosContratuaisRespDTO.setDirecaoDesc(tipoRelacionamentoAtual.getInstituicao().getNome());
    dadosContratuaisRespDTO.setSeccaoId(tipoRelacionamentoAtual.getSeccao().getId());
    dadosContratuaisRespDTO.setSeccaoDesc(tipoRelacionamentoAtual.getSeccao().getNome());
    //dadosContratuaisRespDTO.setCentroCusto(tipoRelacionamentoAtual.getce);

    dadosContratuaisRespDTO.setCarreiraId(tipoRelacionamentoAtual.getCarrPcc().getId());
    dadosContratuaisRespDTO.setCarreiraDesc(tipoRelacionamentoAtual.getCarrPcc().getNome());
    dadosContratuaisRespDTO.setCategoriaId(tipoRelacionamentoAtual.getCategoria().getId());
    dadosContratuaisRespDTO.setCategoriaDesc(tipoRelacionamentoAtual.getCategoria().getNome());
    dadosContratuaisRespDTO.setEscalaoReferenciaId(tipoRelacionamentoAtual.getEscalao().getId());
    dadosContratuaisRespDTO.setEscalaoReferenciaDesc(tipoRelacionamentoAtual.getEscalao().getCodigo());
    dadosContratuaisRespDTO.setTipoVinculoLaboralId(tipoRelacionamentoAtual.getVinculo().getId());
    dadosContratuaisRespDTO.setTipoVinculoLaboralDesc(tipoRelacionamentoAtual.getVinculo().getNome());
    dadosContratuaisRespDTO.setSalario(tipoRelacionamentoAtual.getSalario());
    dadosContratuaisRespDTO.setMoeda(tipoRelacionamentoAtual.getMoeda());
    dadosContratuaisRespDTO.setDataInicio(tipoRelacionamentoAtual.getDataInicio());
    dadosContratuaisRespDTO.setDataFim(tipoRelacionamentoAtual.getDataFim());
    dadosContratuaisRespDTO.setDuracaoMeses(tipoRelacionamentoAtual.getContrato().getDuracao());
    dadosContratuaisRespDTO.setLocalTrabalhoId(tipoRelacionamentoAtual.getLocTrab().getId());
    dadosContratuaisRespDTO.setLocalTrabalhoDesc(tipoRelacionamentoAtual.getLocTrab().getNome());
    dadosContratuaisRespDTO.setRegimeTrabalho(tipoRelacionamentoAtual.getRegime());

    List<EncargosDescontosRespDTO> encargosDescontosList = funcionario.getDefPagamentos() == null ?
        List.of() : funcionario.getDefPagamentos().stream()
        .map(d -> {
          var encargosDescontosRespDTO = new EncargosDescontosRespDTO();
          encargosDescontosRespDTO.setId(d.getId());
          encargosDescontosRespDTO.setTipoEncargoId(d.getTipoMovimento().getId());
          encargosDescontosRespDTO.setTipoEncargoDesc(d.getTipoMovimento().getDescricao());
          encargosDescontosRespDTO.setValor(d.getValor());
          encargosDescontosRespDTO.setDataInicio(d.getDataInicio());
          encargosDescontosRespDTO.setDataFim(d.getDataFim());
          return encargosDescontosRespDTO;
        })
        .toList();
    dadosContratuaisRespDTO.setEncargosDescontos(encargosDescontosList);

    List<SubsidioRespDTO> subsidiosList = funcionario.getDefinicaoRemuneracoes() == null ?
        List.of() : funcionario.getDefinicaoRemuneracoes().stream()
        .map(d -> {
          var subsidioRespDTO = new SubsidioRespDTO();
          subsidioRespDTO.setId(d.getId());
          subsidioRespDTO.setValor(d.getValor());
          subsidioRespDTO.setPercentagem(d.getPercentagem());
          subsidioRespDTO.setTipoSubsidioId(d.getTipoMovimento().getId());
          subsidioRespDTO.setTipoSubsidioDesc(d.getTipoMovimento().getDescricao());
          return subsidioRespDTO;
        })
        .toList();
    dadosContratuaisRespDTO.setSubsidios(subsidiosList);

    return dadosContratuaisRespDTO;
  }


  //new implementation
  public FuncionarioResponseDTO toResponseDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

    FuncionarioResponseDTO dto = new FuncionarioResponseDTO();

    DadosPessoaisRespDTO dadosPessoais = new DadosPessoaisRespDTO();
    dadosPessoais.setId(entity.getId());
    dadosPessoais.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    dadosPessoais.setNome(entity.getNome());
    dadosPessoais.setDataNascimento(entity.getDataNascimento());
    dadosPessoais.setGenero(entity.getSexo());
    dadosPessoais.setNomeMae(entity.getNmMae());
    dadosPessoais.setNomePai(entity.getNmPai());
    dadosPessoais.setEstadoCivil(entity.getEstadoCivil());
    dadosPessoais.setNacionalidade(entity.getNacionalidade());
    dadosPessoais.setTipoDocumentoId(entity.getTipoDocumentoId() != null ? entity.getTipoDocumentoId().getId() : null);
    dadosPessoais.setNumDocumento(entity.getNumDocumento());
    dadosPessoais.setNif(entity.getNif() != null ? entity.getNif() : null);
    dadosPessoais.setNumSegurado(entity.getNuSegInps());
    if (entity.getLocNascId() != null) {
      dadosPessoais.setNaturalidadeId(entity.getLocNascId().getId());
      dadosPessoais.setNaturalidadeDesc(entity.getLocNascId().getNome());
    }

    if (entity.getEndereco() != null) {
      EnderecoRespDTO er = new EnderecoRespDTO();
      er.setId(entity.getEndereco().getId());
      if (entity.getEndereco().getPaisId() != null) {
        er.setPaisId(entity.getEndereco().getPaisId().getId() != null ? entity.getEndereco().getPaisId().getId().intValue() : null);
        er.setPaisDesc(entity.getEndereco().getPaisId().getNome());
      }
      if (entity.getEndereco().getIlhaId() != null) {
        er.setIlhaId(entity.getEndereco().getIlhaId().getId() != null ? entity.getEndereco().getIlhaId().getId().intValue() : null);
        er.setIlhaDesc(entity.getEndereco().getIlhaId().getNome());
      }
      if (entity.getEndereco().getConcelhoId() != null) {
        er.setConcelhoId(entity.getEndereco().getConcelhoId().getId() != null ? entity.getEndereco().getConcelhoId().getId().intValue() : null);
        er.setConcelhoDesc(entity.getEndereco().getConcelhoId().getNome());
      }
      if (entity.getEndereco().getFreguesiaId() != null) {
        er.setFreguesiaId(entity.getEndereco().getFreguesiaId().getId() != null ? entity.getEndereco().getFreguesiaId().getId().intValue() : null);
        er.setFreguesiaDesc(entity.getEndereco().getFreguesiaId().getNome());
      }
      if (entity.getEndereco().getZonaId() != null) {
        er.setZonaId(entity.getEndereco().getZonaId().getId() != null ? entity.getEndereco().getZonaId().getId().intValue() : null);
        er.setZonaDesc(entity.getEndereco().getZonaId().getNome());
      }
      er.setMorada(entity.getEndereco().getMorada());
      er.setEstado(entity.getEndereco().getEstado() != null ? entity.getEndereco().getEstado().getDescription() : null);
      er.setUuid(entity.getEndereco().getUuid() != null ? entity.getEndereco().getUuid().toString() : null);
      dadosPessoais.setEndereco(er);
    }

    if (entity.getContactos() != null) {
      List<ContactoRespDTO> contactos = entity.getContactos().stream().map(c -> {
        ContactoRespDTO cr = new ContactoRespDTO();
        cr.setId(c.getId());
        cr.setUuid(c.getUuid() != null ? c.getUuid().toString() : null);
        cr.setTipoContacto(c.getTipoContacto());
        cr.setContacto(c.getContacto());
        cr.setEstado(c.getEstado() != null ? c.getEstado().getDescription() : null);
        return cr;
      }).toList();
      dadosPessoais.setContactos(contactos);
    }
    dto.setDadosPessoais(dadosPessoais);

    if (entity.getFamiliares() != null) {
      List<AgregadoDependenteRespDTO> familiares = entity.getFamiliares().stream().map(f -> {
        AgregadoDependenteRespDTO fr = new AgregadoDependenteRespDTO();
        fr.setId(f.getId());
        fr.setTipoDocumentoId(f.getTpDocumento() != null ? f.getTpDocumento().getId() : null);
        fr.setTipoDocumentoDesc(f.getTpDocumento() != null ? f.getTpDocumento().getNome() : null);
        fr.setNumDocumento(f.getNumDocumento());
        fr.setNome(f.getNome());
        fr.setDataNascimento(f.getDataNascimento());
        fr.setGenero(f.getSexo());
        fr.setGrauParentesco(f.getGdpId());
        fr.setDependente(f.getDependencia());
        fr.setAgregada(f.getMembroAgr());
        fr.setEstado(f.getEstado() != null ? f.getEstado().name() : null);
        return fr;
      }).toList();
      dto.setFamiliares(familiares);
    }

    DadosAcademicosProfResponseDTO dap = new DadosAcademicosProfResponseDTO();
    if (entity.getHabilitacoesLiterarias() != null) {
      List<HabilitacaoLiterariaRespDTO> habs = entity.getHabilitacoesLiterarias().stream().map(h -> {
        HabilitacaoLiterariaRespDTO hr = new HabilitacaoLiterariaRespDTO();
        hr.setId(h.getId());
        hr.setPaisId(h.getPaisId() != null ? h.getPaisId().getId() != null ? h.getPaisId().getId().intValue() : null : null);
        hr.setPaisDesc(h.getPaisId() != null ? h.getPaisId().getNome() : null);
        hr.setEstabelecimento(h.getEstabelecimento());
        hr.setArea(h.getArea());
        hr.setCurso(h.getNomeCurso());
        hr.setGrauAcademico(h.getNivel());
        hr.setDataInicio(h.getDataInicio());
        hr.setDataTermino(h.getDataFim());
        hr.setConcluido(h.getConcluido());
        return hr;
      }).toList();
      dap.setHabilitacoesLiterarias(habs);
    }

    if (entity.getFormacoesFeitas() != null) {
      List<FormacaoProfissionalRespDTO> forms = entity.getFormacoesFeitas().stream().map(f -> {
        FormacaoProfissionalRespDTO fr = new FormacaoProfissionalRespDTO();
        fr.setId(f.getId());
        fr.setUuid(f.getUuid() != null ? f.getUuid().toString() : null);
        fr.setPaisId(f.getPaisId() != null ? f.getPaisId().getId() : null);
        fr.setPaisDesc(f.getPaisId() != null ? f.getPaisId().getNome() : null);
        fr.setEstabelecimento(f.getEstabelecimento());
        fr.setTipoFormacao(f.getRhtpfor());
        fr.setDesignacao(f.getCurso());
        fr.setNivel(f.getNivel());
        fr.setEstado(f.getEstado() != null ? f.getEstado().name() : null);
        return fr;
      }).toList();
      dap.setFormacoesFeitas(forms);
    }

    if (entity.getExperienciasProfissionais() != null) {
      List<ExperienciaProfissionalRespDTO> exps = entity.getExperienciasProfissionais().stream().map(e -> {
        ExperienciaProfissionalRespDTO er = new ExperienciaProfissionalRespDTO();
        er.setId(e.getId());
        er.setPaisId(e.getPaisId() != null ? e.getPaisId().getId() : null);
        er.setPaisDesc(e.getPaisId() != null ? e.getPaisId().getNome() : null);
        er.setUuid(e.getUuid() != null ? e.getUuid().toString() : null);
        er.setEmpresa(e.getEmpresa());
        er.setCargo(e.getCargo());
        er.setDataEntrada(e.getDataInicio());
        er.setDataSaida(e.getDataFim());
        er.setObservacoes(e.getObservacao());
        er.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
        return er;
      }).toList();
      dap.setExperienciasProfssionais(exps);
    }
    dto.setDadosAcademicosProf(dap);

    if (entity.getDocumentos() != null) {
      List<AnexoRespDTO> anexos = entity.getDocumentos().stream().map(d -> {
        AnexoRespDTO ar = new AnexoRespDTO();
        ar.setId(d.getId());
        ar.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        ar.setTipoDocumentoDesc(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getNome() : null);
        ar.setDocumento(d.getReferenciaName());
        return ar;
      }).toList();
      dto.setAnexos(anexos);
    }

    if (entity.getDadosBancarios() != null) {
      List<DadosBancariosRespDTO> bancos = entity.getDadosBancarios().stream().map(b -> {
        DadosBancariosRespDTO br = new DadosBancariosRespDTO();
        br.setId(b.getId());
        br.setEntidadeBancariaId(b.getRhbId() != null ? b.getRhbId().getId() : null);
        br.setEntidadeBancariaDesc(b.getRhbId() != null ? b.getRhbId().getNmBanco() : null);
        br.setNumConta(b.getNumConta());
        br.setDataInicio(b.getDataInicio());
        br.setDataFim(b.getDataFim());
        return br;
      }).toList();
      dto.setDadosBancarios(bancos);
    }

    if (entity.getTiposrelacionamentos() != null && !entity.getTiposrelacionamentos().isEmpty()) {
      var dcr = dadosContratuaisMapper.dadosContratuaisRespDTO(entity);
      dto.setDadosContratuais(dcr);
    }


    return dto;
  }

  public FuncionarioEntity toEntity(DadosPessoaisReqDTO dadosPessoais, Estado estado) {
    if (dadosPessoais == null) return null;
    FuncionarioEntity fun = new FuncionarioEntity();

    var tipoDocumento = entityManager.getReference(TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId());

    fun.setIdColaborador(dadosPessoais.getIdColaborador());
    fun.setUuid(UuidCreator.getTimeOrderedEpoch());
    fun.setEstado(estado != null ? estado : Estado.P);
    fun.setEstadoValidacao(estado != null ? estado.name() : "P");
    fun.setTipoDocumentoId(tipoDocumento);
    fun.setNumDocumento(dadosPessoais.getNumDocumento());
    fun.setNome(dadosPessoais.getNome());
    fun.setFotografia(dadosPessoais.getUrlFoto());
    fun.setDataNascimento(dadosPessoais.getDataNascimento());
    fun.setSexo(dadosPessoais.getGenero());
    fun.setNmMae(dadosPessoais.getNomeMae());
    fun.setNmPai(dadosPessoais.getNomePai());
    fun.setEstadoCivil(dadosPessoais.getEstadoCivil());
    fun.setNacionalidade(dadosPessoais.getNacionalidade());
    fun.setLocNascId(entityManager.getReference(GeografiaEntity.class, dadosPessoais.getNaturalidadeId()));
    fun.setNif(dadosPessoais.getNif());
    fun.setNuSegInps(dadosPessoais.getNumSegurado());

    DocumentoPessoalEntity docPessoal = new DocumentoPessoalEntity();
    docPessoal.setEstado(estado != null ? estado : Estado.P);
    docPessoal.setFunId(fun);
    docPessoal.setTipoDocumentoId(tipoDocumento);
    docPessoal.setNumDocumento(dadosPessoais.getNumDocumento());
    fun.setDocumentoPessoal(docPessoal);

    if (dadosPessoais.getEndereco() != null) {
      var e = enderecoMapper.toEntity(dadosPessoais.getEndereco(), Estado.P);
      e.setFunId(fun);
      fun.setEndereco(e);
    }

    if (dadosPessoais.getContactos() != null) {
      var list = dadosPessoais.getContactos().stream().map(c -> {
        var ce = contactoMapper.toEntity(c, Estado.P);
        ce.setFunId(fun);
        return ce;
      }).toList();
      fun.setContactos(list);
    }


    return fun;
  }

  public FuncionarioEntity toUpdateEntity(FuncionarioEntity funParam, DadosPessoaisReqDTO dadosPessoais) {
    if (dadosPessoais == null) return null;
    if (funParam == null) return null;

    var tipoDocumento = entityManager.getReference(TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId());

    funParam.setIdColaborador(dadosPessoais.getIdColaborador());
    funParam.setTipoDocumentoId(tipoDocumento);
    funParam.setNumDocumento(dadosPessoais.getNumDocumento());
    funParam.setNome(dadosPessoais.getNome());
    funParam.setFotografia(dadosPessoais.getUrlFoto());
    funParam.setDataNascimento(dadosPessoais.getDataNascimento());
    funParam.setSexo(dadosPessoais.getGenero());
    funParam.setNmMae(dadosPessoais.getNomeMae());
    funParam.setNmPai(dadosPessoais.getNomePai());
    funParam.setEstadoCivil(dadosPessoais.getEstadoCivil());
    funParam.setNacionalidade(dadosPessoais.getNacionalidade());
    funParam.setLocNascId(entityManager.getReference(GeografiaEntity.class, dadosPessoais.getNaturalidadeId()));
    funParam.setNif(dadosPessoais.getNif());
    funParam.setNuSegInps(dadosPessoais.getNumSegurado());

    DocumentoPessoalEntity docPessoal = funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal() : new DocumentoPessoalEntity();
    docPessoal.setFunId(funParam);
    docPessoal.setTipoDocumentoId(tipoDocumento);
    docPessoal.setNumDocumento(dadosPessoais.getNumDocumento());
    docPessoal.setUuid(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getUuid() : IdentificadorUnico.create().getValor());
    docPessoal.setEstado(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getEstado() : Estado.P);
    funParam.setDocumentoPessoal(docPessoal);


    EnderecoEntity e = funParam.getEndereco() != null ?
        enderecoMapper.toUpdateEntity(funParam.getEndereco(), dadosPessoais.getEndereco())
        : enderecoMapper.toEntity(dadosPessoais.getEndereco(), Estado.P);


    e.setFunId(funParam);

    return funParam;

  }




}
