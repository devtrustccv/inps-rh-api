package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.*;
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

import java.util.List;

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


  public DadosPessoaisRespDTO toDadosPessoaisRespDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

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

    return dadosPessoais;
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
    docPessoal.setUuid(UuidCreator.getTimeOrderedEpoch());
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
