package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.util.InpsStringUtils;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {


  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final EntityManager entityManager;
  private final FamiliarMapper familiarMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;

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
    dadosPessoais.setLocalidade(entity.getLocalidade());
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

    dadosPessoais.setEstado(entity.getEstado() != null ? entity.getEstado().getCode() : null);
    dadosPessoais.setEstadoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);

    return dadosPessoais;
  }


  public FuncionarioResponseDTO toResponseDTO(FuncionarioEntity entity) {
    if (entity == null) return null;

    FuncionarioResponseDTO dto = new FuncionarioResponseDTO();

    DadosPessoaisRespDTO dadosPessoais = toDadosPessoaisRespDTO(entity);
    dto.setDadosPessoais(dadosPessoais);

    // Filhos no MESMO estado do pai (funcionario). O estado da própria entidade decide o snapshot —
    // Pendente (P) / Em correção (C) durante a validação, Activo (A) depois — sem flag do caller. Os
    // filhos andam em lockstep com o pai (ver mudaEstado), logo "filhos no estado do pai" é sempre o
    // snapshot correcto e exclui automaticamente os eliminados (E).
    var permitidos = List.of(entity.getEstado());

    if (entity.getFamiliares() != null) {
      var familiaresFiltrados = entity.getFamiliares().stream()
          .filter(f -> permitidos.contains(f.getEstado())).toList();
      dto.setFamiliares(familiarMapper.toAgregadoDependenteRespDTOList(familiaresFiltrados));
    }

    DadosAcademicosProfResponseDTO dap = new DadosAcademicosProfResponseDTO();
    if (entity.getHabilitacoesLiterarias() != null) {
      var habFiltradas = entity.getHabilitacoesLiterarias().stream()
          .filter(h -> permitidos.contains(h.getEstado())).toList();
      dap.setHabilitacoesLiterarias(habilitacaoLiterariaMapper.toHabilitacaoLiterariaRespDTOList(habFiltradas));
    }

    if (entity.getFormacoesFeitas() != null) {
      var formFiltradas = entity.getFormacoesFeitas().stream()
          .filter(f -> permitidos.contains(f.getEstado())).toList();
      dap.setFormacoesFeitas(formacaoFeitaMapper.toFormacaoFeitaRespDTOList(formFiltradas));
    }

    if (entity.getExperienciasProfissionais() != null) {
      var expFiltradas = entity.getExperienciasProfissionais().stream()
          .filter(e -> permitidos.contains(e.getEstado())).toList();
      dap.setExperienciasProfssionais(experienciaProfissionalMapper.toExperienciaProfissionalRespDTOList(expFiltradas));
    }
    dto.setDadosAcademicosProf(dap);

    if (entity.getDadosBancarios() != null) {
      var bancFiltrados = entity.getDadosBancarios().stream()
          .filter(b -> permitidos.contains(b.getEstado())).toList();
      dto.setDadosBancarios(dadosBancariosMapper.toDadosBancariosRespDTOList(bancFiltrados));
    }

    return dto;
  }

  public FuncionarioEntity toEntity(DadosPessoaisReqDTO dadosPessoais, Estado estado) {
    if (dadosPessoais == null) return null;
    FuncionarioEntity fun = new FuncionarioEntity();

    var tipoDocumento = ValidationUtil.ref(entityManager, TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId());

    fun.setIdColaborador(dadosPessoais.getIdColaborador());
    fun.setUuid(UuidCreator.getTimeOrderedEpoch());
    fun.setEstado(estado != null ? estado : Estado.P);
    fun.setEstadoValidacao(estado != null ? estado.name() : "P");
    fun.setTipoDocumentoId(tipoDocumento);
    fun.setNumDocumento(ValidationUtil.trimToNull(dadosPessoais.getNumDocumento()));

    var name = ValidationUtil.trimToNull(dadosPessoais.getNome());
    fun.setNome(name);
    var normalizedName = InpsStringUtils.normalizeText(name);
    fun.setNomeNormalizado(normalizedName != null ? normalizedName.toLowerCase(Locale.ROOT) : name);

    fun.setFotografia(ValidationUtil.trimToNull(dadosPessoais.getUrlFoto()));
    fun.setDataNascimento(dadosPessoais.getDataNascimento());
    fun.setSexo(ValidationUtil.trimToNull(dadosPessoais.getGenero()));
    fun.setNmPai(ValidationUtil.trimToNull(dadosPessoais.getNomePai()));
    fun.setNmMae(ValidationUtil.trimToNull(dadosPessoais.getNomeMae()));
    fun.setEstadoCivil(ValidationUtil.trimToNull(dadosPessoais.getEstadoCivil()));
    fun.setLocalidade(ValidationUtil.trimToNull(dadosPessoais.getLocalidade()));
    fun.setNacionalidade(ValidationUtil.trimToNull(dadosPessoais.getNacionalidade()));
    fun.setLocNascId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dadosPessoais.getNaturalidadeId()));
    fun.setNif(dadosPessoais.getNif());
    fun.setNuSegInps(ValidationUtil.trimToNull(dadosPessoais.getNumSegurado()));

    DocumentoPessoalEntity docPessoal = new DocumentoPessoalEntity();
    docPessoal.setEstado(estado != null ? estado : Estado.P);
    docPessoal.setFunId(fun);
    docPessoal.setTipoDocumentoId(tipoDocumento);
    docPessoal.setNumDocumento(dadosPessoais.getNumDocumento());
    docPessoal.setDataEmissao(dadosPessoais.getDataEmissaoDocumento());
    docPessoal.setDataValidade(dadosPessoais.getDataValidadeDocumento());
    docPessoal.setLocEmissId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dadosPessoais.getLocalEmissaoDocumentoId()));
    docPessoal.setUuid(UuidCreator.getTimeOrderedEpoch());
    fun.setDocumentoPessoal(docPessoal);

    if (dadosPessoais.getEndereco() != null) {
      var e = enderecoMapper.toEntity(dadosPessoais.getEndereco(), Estado.P);
      e.setFunId(fun);
      fun.setEndereco(e);
    }

    if (dadosPessoais.getContactos() != null) {
      var list = dadosPessoais.getContactos().stream().map(c -> contactoMapper.toEntity(c, Estado.P, fun)).toList();
      fun.setContactos(list);
    }


    return fun;
  }

  public FuncionarioEntity toUpdateEntity(FuncionarioEntity funParam, DadosPessoaisReqDTO dadosPessoais) {
    if (dadosPessoais == null) return null;
    if (funParam == null) return null;

    var tipoDocumento = ValidationUtil.ref(entityManager, TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId());

    funParam.setIdColaborador(dadosPessoais.getIdColaborador());
    funParam.setNumDocumento(ValidationUtil.trimToNull(dadosPessoais.getNumDocumento()));
    funParam.setTipoDocumentoId(tipoDocumento);

    var name = ValidationUtil.trimToNull(dadosPessoais.getNome());
    funParam.setNome(name);
    var normalizedName = InpsStringUtils.normalizeText(name);
    funParam.setNomeNormalizado(normalizedName != null ? normalizedName.toLowerCase(Locale.ROOT) : name);

    funParam.setFotografia(ValidationUtil.trimToNull(dadosPessoais.getUrlFoto()));
    funParam.setSexo(ValidationUtil.trimToNull(dadosPessoais.getGenero()));
    funParam.setDataNascimento(dadosPessoais.getDataNascimento());
    funParam.setNmMae(ValidationUtil.trimToNull(dadosPessoais.getNomeMae()));
    funParam.setNmPai(ValidationUtil.trimToNull(dadosPessoais.getNomePai()));
    funParam.setEstadoCivil(ValidationUtil.trimToNull(dadosPessoais.getEstadoCivil()));
    funParam.setNacionalidade(ValidationUtil.trimToNull(dadosPessoais.getNacionalidade()));
    funParam.setLocalidade(ValidationUtil.trimToNull(dadosPessoais.getLocalidade()));
    funParam.setLocNascId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dadosPessoais.getNaturalidadeId()));
    funParam.setNif(dadosPessoais.getNif());
    funParam.setNuSegInps(ValidationUtil.trimToNull(dadosPessoais.getNumSegurado()));

    DocumentoPessoalEntity docPessoal = funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal() : new DocumentoPessoalEntity();
    // Emissão/validade/local only come from the document search: keep the stored
    // values when the same document is resubmitted without them, reset when the
    // document itself changed.
    boolean mesmoDocumento = docPessoal.getId() != null
        && Objects.equals(docPessoal.getNumDocumento(), dadosPessoais.getNumDocumento())
        && docPessoal.getTipoDocumentoId() != null
        && Objects.equals(docPessoal.getTipoDocumentoId().getId(), dadosPessoais.getTipoDocumentoId());
    docPessoal.setFunId(funParam);
    docPessoal.setTipoDocumentoId(tipoDocumento);
    docPessoal.setNumDocumento(dadosPessoais.getNumDocumento());
    if (!mesmoDocumento || dadosPessoais.getDataEmissaoDocumento() != null) {
      docPessoal.setDataEmissao(dadosPessoais.getDataEmissaoDocumento());
    }
    if (!mesmoDocumento || dadosPessoais.getDataValidadeDocumento() != null) {
      docPessoal.setDataValidade(dadosPessoais.getDataValidadeDocumento());
    }
    if (!mesmoDocumento || dadosPessoais.getLocalEmissaoDocumentoId() != null) {
      docPessoal.setLocEmissId(ValidationUtil.ref(entityManager, GeografiaEntity.class, dadosPessoais.getLocalEmissaoDocumentoId()));
    }
    docPessoal.setUuid(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getUuid() : IdentificadorUnico.create().valor());
    docPessoal.setEstado(funParam.getDocumentoPessoal() != null ? funParam.getDocumentoPessoal().getEstado() : Estado.P);
    funParam.setDocumentoPessoal(docPessoal);


    EnderecoEntity e = funParam.getEndereco() != null ?
        enderecoMapper.toUpdateEntity(funParam.getEndereco(), dadosPessoais.getEndereco())
        : enderecoMapper.toEntity(dadosPessoais.getEndereco(), Estado.P);

    if (e != null) {
      e.setFunId(funParam);
      funParam.setEndereco(e);
    }

    return funParam;

  }


}
