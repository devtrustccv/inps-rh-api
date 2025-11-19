package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.github.f4b6a3.uuid.UuidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class DossierService {

  private final EnderecoMapper enderecoMapper;
  private final ContactoMapper contactoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitationLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final ContratuaisEntityMapper contratuaisEntityMapper;
  private final FuncionarioMapper funcionarioMapper;

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;


  @PersistenceContext
  private EntityManager entityManager;


  @Transactional
  public FuncionarioResponseDTO saveDossierColaborador(CreateFuncionarioCommand command) {
    FuncionarioRequestDTO dto = command.getFuncionariorequest();

    var dadosPessoais = dto.getDadosPessoais();

    FuncionarioEntity fun = new FuncionarioEntity();
    fun.setIdColaborador(dadosPessoais.getIdColaborador());
    fun.setUuid(UuidCreator.getTimeOrderedEpoch());
    fun.setEstado(Estado.P);
    fun.setEstadoValidacao("P");
    fun.setTipoDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId()));
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
    docPessoal.setEstado(Estado.P);
    docPessoal.setFunId(fun);
    docPessoal.setTipoDocumentoId(entityManager.getReference(TipoDocumentoEntity.class, dadosPessoais.getTipoDocumentoId()));
    docPessoal.setNumDocumento(dadosPessoais.getNumDocumento());
    fun.setDocumentoPessoal(docPessoal);

    if (dadosPessoais.getEndereco() != null) {
      var e = enderecoMapper.toEntity(dadosPessoais.getEndereco());
      e.setEstado(Estado.P);
      e.setFunId(fun);
      fun.setEndereco(e);
    }

    if (dadosPessoais.getContactos() != null) {
      var list = dadosPessoais.getContactos().stream().map(c -> {
        var ce = contactoMapper.toEntity(c);
        ce.setEstado(Estado.P);
        ce.setFunId(fun);
        return ce;
      }).toList();
      fun.setContactos(list);
    }

    if (dto.getFamiliares() != null) {
      var list = dto.getFamiliares().stream().map(f -> {
        var fe = familiarMapper.toEntity(f);
        fe.setEstado(Estado.P);
        fe.setFunId(fun);
        return fe;
      }).toList();
      fun.setFamiliares(list);
    }

    if (dto.getDadosAcademicosProf() != null) {
      var da = dto.getDadosAcademicosProf();
      if (da.getHabilitacoesLiterarias() != null) {
        var list = da.getHabilitacoesLiterarias().stream().map(h -> {
          var he = habilitationLiterariaMapper.toEntity(h);
          he.setEstado(Estado.P);
          he.setFunId(fun);
          return he;
        }).toList();
        fun.setHabilitacoesLiterarias(list);
      }

      if (da.getFormacoesFeitas() != null) {
        var list = da.getFormacoesFeitas().stream().map(f -> {
          var fe = formacaoFeitaMapper.toEntity(f);
          fe.setEstado(Estado.P);
          fe.setFunId(fun);
          return fe;
        }).toList();
        fun.setFormacoesFeitas(list);
      }

      if (da.getExperienciasProfssionais() != null) {
        var list = da.getExperienciasProfssionais().stream().map(e -> {
          var ee = experienciaProfissionalMapper.toEntity(e);
          ee.setEstado(Estado.P);
          ee.setFunId(fun);
          return ee;
        }).toList();
        fun.setExperienciasProfissionais(list);
      }
    }

    if (dto.getAnexos() != null) {
      var list = dto.getAnexos().stream().map(a -> {
        var de = documentoMapper.toEntity(a);
        de.setEstado(Estado.P);
        de.setFunId(fun);
        return de;
      }).toList();
      fun.setDocumentos(list);
    }

    if (dto.getDadosBancarios() != null) {
      var list = dto.getDadosBancarios().stream().map(b -> {
        var be = dadosBancariosMapper.toEntity(b);
        be.setEstado(Estado.P);
        be.setFunId(fun);
        return be;
      }).toList();
      fun.setDadosBancarios(list);
    }

    var dc = dto.getDadosContratuais();
    if (dc == null) {
      throw IgrpResponseStatusException.badRequest("Dados contratuais obrigatórios");
    }

    var contrato = contratuaisEntityMapper.toContrato(dc);
    contrato.setEstado(Estado.P);
    contrato.setFunId(fun);
    contrato.setVersao(1);
    fun.setContratos(java.util.List.of(contrato));

    var carreira = contratuaisEntityMapper.toCarreira(dc);
    if (carreira != null) {
      carreira.setEstado(Estado.P);
      carreira.setFunId(fun);
      fun.setCarreiras(java.util.List.of(carreira));
    }

    var regime = contratuaisEntityMapper.toRegime(dc);
    if (regime != null) {
      regime.setEstado(Estado.P);
      regime.setFunId(fun);
      fun.setRegimesTrabalhos(java.util.List.of(regime));
    }

    var mobilidade = contratuaisEntityMapper.toMobilidade(dc);
    if (mobilidade != null) {
      mobilidade.setEstado(Estado.P);
      mobilidade.setFunId(fun);
      fun.setMobilidades(java.util.List.of(mobilidade));
    }

    if (dc.getSubsidios() != null && !dc.getSubsidios().isEmpty()) {
      var remList = dc.getSubsidios().stream()
          .map(s -> contratuaisEntityMapper.toDefinicaoRemuneracao(s, fun))
          .toList();
      fun.setDefinicoesRenumeracoes(remList);
    }

    if (dc.getEncargosDescontos() != null && !dc.getEncargosDescontos().isEmpty()) {
      var pagList = dc.getEncargosDescontos().stream()
          .map(e -> contratuaisEntityMapper.toDefPagamento(e, fun))
          .toList();
      fun.setDefinicoesPagamentos(pagList);
    }

    var tr = contratuaisEntityMapper.toRelacionamento(dc);
    tr.setEstado(Estado.P);
    tr.setFunId(fun);
    tr.setContratoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    fun.setTiposrelacionamentos(java.util.List.of(tr));

    var param = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    var sl = contratuaisEntityMapper.toSituacaoLaboralInicial(dc, param);
    sl.setEstado(Estado.P);
    sl.setFunId(fun);
    fun.setSituacoesLaborais(java.util.List.of(sl));


    var valid = contratuaisEntityMapper.toValidacaoInsert("REGISTO_COLABORADOR", 1L); //todo resolve id later
    valid.setEstado(Estado.P);
    valid.setFunId(fun);
    valid.setTiprelId(tr);
    fun.setValidacoes(java.util.List.of(valid));

    FuncionarioEntity saved = funcionarioEntityRepository.save(fun);
    return funcionarioMapper.toResponseDTO(saved);
  }



}
