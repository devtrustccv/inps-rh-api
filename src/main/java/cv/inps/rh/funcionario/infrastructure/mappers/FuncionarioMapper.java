package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {


  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final EntityManager entityManager;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final FamiliarMapper familiarMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;




  public DadosPessoaisRespDTO toDadosPessoaisRespDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

    DadosPessoaisRespDTO dadosPessoais = new DadosPessoaisRespDTO();
    dadosPessoais.setId(entity.getId());
    dadosPessoais.setIdColaborador(entity.getIdColaborador());
    dadosPessoais.setUuid(entity.getUuid() != null ? entity.getUuid().toString() : null);
    dadosPessoais.setNome(entity.getNome());
    dadosPessoais.setUrlFoto(entity.getFotografia());
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
      EnderecoRespDTO er = enderecoMapper.respDTO(entity.getEndereco());
      dadosPessoais.setEndereco(er);
    }

    if (entity.getContactos() != null) {
      List<ContactoRespDTO> contactos = contactoMapper.respDTOList(entity.getContactos());
      dadosPessoais.setContactos(contactos);
    }

    return dadosPessoais;
  }


  public FuncionarioResponseDTO toResponseDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

    FuncionarioResponseDTO dto = new FuncionarioResponseDTO();

    DadosPessoaisRespDTO dadosPessoais = toDadosPessoaisRespDTO(entity);
    dto.setDadosPessoais(dadosPessoais);


    if (entity.getFamiliares() != null) {
      List<AgregadoDependenteRespDTO> familiares = familiarMapper.toAgregadoDependenteRespDTOList(entity.getFamiliares());
      dto.setFamiliares(familiares);
    }

    DadosAcademicosProfResponseDTO dap = new DadosAcademicosProfResponseDTO();
    if (entity.getHabilitacoesLiterarias() != null) {
      var habilitacaoLiterariaRespDTOS = habilitacaoLiterariaMapper.toHabilitacaoLiterariaRespDTOList(entity.getHabilitacoesLiterarias());
      dap.setHabilitacoesLiterarias(habilitacaoLiterariaRespDTOS);
    }

    if (entity.getFormacoesFeitas() != null) {
      var formacaoFeitaRespDTOS = formacaoFeitaMapper.toFormacaoFeitaRespDTOList(entity.getFormacoesFeitas());
      dap.setFormacoesFeitas(formacaoFeitaRespDTOS);
    }

    if (entity.getExperienciasProfissionais() != null) {
      var experienciaProfissionalRespDTOS = experienciaProfissionalMapper.toExperienciaProfissionalRespDTOList(entity.getExperienciasProfissionais());
      dap.setExperienciasProfssionais(experienciaProfissionalRespDTOS);
    }
    dto.setDadosAcademicosProf(dap);


    if (entity.getDadosBancarios() != null) {
      var dadosBancariosRespDTOS = dadosBancariosMapper.toDadosBancariosRespDTOList(entity.getDadosBancarios());
      dto.setDadosBancarios(dadosBancariosRespDTOS);
    }

    if (entity.getDocumentos() != null) {
      var anexos = documentoMapper.toAnexoRespDTOList(entity.getDocumentos());
      dto.setAnexos(anexos);
    }


    if (entity.getTiposrelacionamentos() != null && !entity.getTiposrelacionamentos().isEmpty()) {
      var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(entity.getUuid());
      var dcr = dadosContratuaisMapper.dadosContratuaisRespDTO(tipoRelacionamentoAtual);
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
    docPessoal.setUuid(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getUuid() : IdentificadorUnico.create().valor());
    docPessoal.setEstado(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getEstado() : Estado.P);
    funParam.setDocumentoPessoal(docPessoal);


    EnderecoEntity e = funParam.getEndereco() != null ?
        enderecoMapper.toUpdateEntity(funParam.getEndereco(), dadosPessoais.getEndereco())
        : enderecoMapper.toEntity(dadosPessoais.getEndereco(), Estado.P);


    e.setFunId(funParam);

    return funParam;

  }




}
