package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDetailsDTO;
import cv.inps.rh.funcionario.domain.models.*;
import cv.inps.rh.shared.infrastructure.mappers.EstadoMapper;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.parametrizacao.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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

  private final EntityManager entityManager;

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

    List<Endereco> enderecos = entity.getEnderecos() != null
        ? entity.getEnderecos().stream()
        .map(enderecoMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

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

    List<ExperienciaProfissional> experienciasProfissionais = entity.getExperienciasProfissionais()!=null?
        entity.getExperienciasProfissionais().stream().map(experienciaProfissionalMapper::toDomain)
            .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<Documento> documentos = entity.getDocumentos()!=null ? entity.getDocumentos().stream()
        .map(documentoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<DadosBancarios> dadosBancarios = entity.getDadosBancarios()!=null ? entity.getDadosBancarios().stream()
        .map(dadosBancariosMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<TiposRelacionamento> tiposRelacionamentos = entity.getTiposrelacionamentos()!=null ? entity.getTiposrelacionamentos().stream()
        .map(tiposRelacionamentoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Contrato> contratos = entity.getContratos()!=null ? entity.getContratos().stream()
        .map(contratoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Carreira> carreiras = entity.getCarreiras()!=null ? entity.getCarreiras().stream()
        .map(carreiraMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<Mobilidade> mobilidades = entity.getMobilidades()!=null ? entity.getMobilidades().stream()
        .map(mobilidadeMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

    List<RegimeTrabalho> regimeTrabalhos = entity.getRegimesTrabalhos()!=null ? entity.getRegimesTrabalhos().stream()
        .map(regimeTrabalhoMapper::toDomain).collect(Collectors.toList()) : new ArrayList<>();

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
        enderecos,
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
        regimeTrabalhos
    );
  }

  public Funcionario toDomainLigth(FuncionarioEntity entity) {
    if (entity == null) return null;

    return Funcionario.rebuildLight(
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
        estadoMapper.fromString(entity.getEstadoValidacao())
    );
  }


  /**
   * Converts domain Funcionario to JPA entity
   */
  public FuncionarioEntity toEntity(Funcionario funcionario) {
    if (funcionario == null) return null;

    FuncionarioEntity entity = new FuncionarioEntity();
    entity.setUuid(funcionario.getUuid().getValor());
    //entity.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? tipoDocumentoMapper.toEntity(funcionario.getTipoDocumento()) : null);
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
   // entity.setLocNascId(funcionario.getLocalNascimento() != null ? geografiaMapper.toEntity(funcionario.getLocalNascimento()) : null);
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

    //enderecos
    if (funcionario.getEnderecos() != null) {
      var enderecosEntities = funcionario.getEnderecos().stream()
          .map(enderecoMapper::toEntity)
          .collect(Collectors.toList());

      enderecosEntities.forEach(e -> e.setFunId(entity));
      entity.setEnderecos(enderecosEntities);
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
    if(funcionario.getFormacoes() != null) {
      var formacoesEntities = funcionario.getFormacoes().stream()
          .map(formacaoFeitaMapper::toEntity)
          .collect(Collectors.toList());

      formacoesEntities.forEach(f -> f.setFunId(entity));
      entity.setFormacoesFeitas(formacoesEntities);
    }

    // experiencias profissionais
    if(funcionario.getExperiencias() != null) {
      var experienciasEntities = funcionario.getExperiencias().stream()
          .map(experienciaProfissionalMapper::toEntity)
          .collect(Collectors.toList());
      experienciasEntities.forEach(e -> e.setFunId(entity));
      entity.setExperienciasProfissionais(experienciasEntities);
    }

    //documentos
    if(funcionario.getDocumentos() != null) {
      var documentosEntities = funcionario.getDocumentos().stream()
          .map(documentoMapper::toEntity)
          .collect(Collectors.toList());
      documentosEntities.forEach(d -> d.setFunId(entity));
      entity.setDocumentos(documentosEntities);
    }

    //dados bancarios
    if(funcionario.getDadosBancarios() != null) {
      var dadosBancariosEntities = funcionario.getDadosBancarios().stream()
          .map(dadosBancariosMapper::toEntity)
          .collect(Collectors.toList());
      dadosBancariosEntities.forEach(d -> d.setFunId(entity));
      entity.setDadosBancarios(dadosBancariosEntities);

    }

    //tipos relacionamentos
    if (funcionario.getTiposRelacionamentos() != null) {
      var tiposRelacionamentosEntities = funcionario.getTiposRelacionamentos().stream()
          .map(tiposRelacionamentoMapper::toEntity)
          .collect(Collectors.toList());
      tiposRelacionamentosEntities.forEach(t -> t.setFunId(entity));
      entity.setTiposrelacionamentos(tiposRelacionamentosEntities);
    }


    // contratos
    if(funcionario.getContratos()!=null) {
      var contratosEntities = funcionario.getContratos().stream()
          .map(contratoMapper::toEntity)
          .collect(Collectors.toList());
      contratosEntities.forEach(c -> c.setFunId(entity));
      entity.setContratos(contratosEntities);
    }

    // carreiras
    if(funcionario.getCarreiras()!=null) {
      var carreirasEntities = funcionario.getCarreiras().stream()
          .map(carreiraMapper::toEntity)
          .collect(Collectors.toList());
      carreirasEntities.forEach(c -> c.setFunId(entity));
      entity.setCarreiras(carreirasEntities);
    }

    //mobilidades
    if(funcionario.getMobilidades()!=null) {
      var mobilidadesEntities = funcionario.getMobilidades().stream()
          .map(mobilidadeMapper::toEntity)
          .collect(Collectors.toList());
      mobilidadesEntities.forEach(m -> m.setFunId(entity));
      entity.setMobilidades(mobilidadesEntities);
    }

    //regimes trabalhos
    if(funcionario.getRegimeTrabalhos()!=null) {
      var regimeTrabalhosEntities = funcionario.getRegimeTrabalhos().stream()
          .map(regimeTrabalhoMapper::toEntity)
          .collect(Collectors.toList());
      regimeTrabalhosEntities.forEach(r -> r.setFunId(entity));
      entity.setRegimesTrabalhos(regimeTrabalhosEntities);
    }

    return entity;
  }

  public FuncionarioResponseDTO toDTO(Funcionario funcionario) {
    if (funcionario == null) return null;

    var dto = new FuncionarioResponseDTO();
    dto.setId(funcionario.getId() != null ? funcionario.getId() : null);
    dto.setUuid(funcionario.getUuid() != null ? funcionario.getUuid().toString() : null);
    dto.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getId().intValue() : null);
    dto.setTipoDocumentoDesc(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getNome() : null);
    dto.setNumDocumento(funcionario.getNumeroDocumento());
    dto.setNome(funcionario.getNomeCompleto());
    dto.setUrlFoto(funcionario.getFotografia());
    dto.setDataNascimento(funcionario.getDataNascimento());
    dto.setGenero(funcionario.getSexo());
    dto.setNomeMae(funcionario.getNomeMae());
    dto.setNomePai(funcionario.getNomePai());
    dto.setEstadoCivil(funcionario.getEstadoCivil());
    dto.setNacionalidade(funcionario.getNacionalidade());
    dto.setNaturalidadeId(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getId() : null);
    dto.setNaturalidadeDesc(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getNome() : null);
    dto.setNif(funcionario.getNumeroFiscal() != null ? funcionario.getNumeroFiscal().toString() : null);
    dto.setNumSegurado(funcionario.getNumeroSegurancaSocial());
    return dto;
  }


  public FuncionarioResponseDetailsDTO toResponseDetailsDTO(Funcionario funcionario) {
    if (funcionario == null) return null;

    var dto = new FuncionarioResponseDetailsDTO();
    dto.setId(funcionario.getId() != null ? funcionario.getId() : null);
    dto.setUuid(funcionario.getUuid() != null ? funcionario.getUuid().toString() : null);
    dto.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getId().intValue() : null);
    dto.setTipoDocumentoDesc(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getNome() : null);
    dto.setNumDocumento(funcionario.getNumeroDocumento());
    dto.setNome(funcionario.getNomeCompleto());
    dto.setUrlFoto(funcionario.getFotografia());
    dto.setDataNascimento(funcionario.getDataNascimento());
    dto.setGenero(funcionario.getSexo());
    dto.setNomeMae(funcionario.getNomeMae());
    dto.setNomePai(funcionario.getNomePai());
    dto.setEstadoCivil(funcionario.getEstadoCivil());
    dto.setNacionalidade(funcionario.getNacionalidade());
    dto.setNaturalidadeId(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getId() : null);
    dto.setNaturalidadeDesc(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getNome() : null);
    dto.setNif(funcionario.getNumeroFiscal() != null ? funcionario.getNumeroFiscal().toString() : null);
    dto.setNumSegurado(funcionario.getNumeroSegurancaSocial());


    // ---- Contactos ----
    if (funcionario.getContactos() != null && !funcionario.getContactos().isEmpty()) {
      dto.setContactos(contactoMapper.toDTOList(funcionario.getContactos()));
    }

    // ---- Endereços ----
    if (funcionario.getEnderecos() != null && !funcionario.getEnderecos().isEmpty()) {
      dto.setEnderecos(enderecoMapper.toDTOList(funcionario.getEnderecos()));
    }


    if (funcionario.getFamiliares() != null && !funcionario.getFamiliares().isEmpty()) {
      dto.setFamiliares(familiarMapper.toResponseDTOList(funcionario.getFamiliares()));
    }

    if (funcionario.getHabilitacaoLiterarias() != null && !funcionario.getHabilitacaoLiterarias().isEmpty()) {
      dto.setHabilitacoesLiterarias(habilitacaoLiterariaMapper.toResponseDTOList(funcionario.getHabilitacaoLiterarias()));
    }

    if(funcionario.getFormacoes() != null && !funcionario.getFormacoes().isEmpty()) {
      dto.setFormacoesFeitas(formacaoFeitaMapper.toResponseDTOList(funcionario.getFormacoes()));
    }

    if(funcionario.getExperiencias()!=null && !funcionario.getExperiencias().isEmpty()) {
      dto.setExperienciasProfssionais(experienciaProfissionalMapper.toResponseDTOList(funcionario.getExperiencias()));
    }

    if(funcionario.getDocumentos()!=null && !funcionario.getDocumentos().isEmpty()) {
      dto.setAnexos(documentoMapper.toResponseDTOList(funcionario.getDocumentos()));
    }

    if(funcionario.getDadosBancarios()!=null && !funcionario.getDadosBancarios().isEmpty()) {
      dto.setDadosBancarios(dadosBancariosMapper.toResponseDTOList(funcionario.getDadosBancarios()));
    }

    return dto;
  }


}
