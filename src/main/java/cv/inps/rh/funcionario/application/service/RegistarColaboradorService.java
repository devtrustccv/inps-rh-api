package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistarColaboradorService {

  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitationLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final FuncionarioMapper funcionarioMapper;
  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;


  @PersistenceContext
  private EntityManager entityManager;


  @Transactional
  public FuncionarioResponseDTO saveDossierColaborador(CreateFuncionarioCommand command) {
    FuncionarioRequestDTO dto = command.getFuncionariorequest();

    var dadosPessoais = dto.getDadosPessoais();

    FuncionarioEntity fun = funcionarioMapper.toEntity(dadosPessoais, Estado.P);

    if (dto.getFamiliares() != null) {
      var list = dto.getFamiliares().stream().map(f -> {
        var fe = familiarMapper.toEntity(f, Estado.P);
        fe.setFunId(fun);
        return fe;
      }).toList();
      fun.setFamiliares(list);
    }

    if (dto.getDadosAcademicosProf() != null) {
      var da = dto.getDadosAcademicosProf();
      if (da.getHabilitacoesLiterarias() != null) {
        var list = da.getHabilitacoesLiterarias().stream().map(h -> {
          var he = habilitationLiterariaMapper.toEntity(h, Estado.P);
          he.setFunId(fun);
          return he;
        }).toList();
        fun.setHabilitacoesLiterarias(list);
      }

      if (da.getFormacoesFeitas() != null) {
        var list = da.getFormacoesFeitas().stream().map(f -> {
          var fe = formacaoFeitaMapper.toEntity(f, Estado.P);
          fe.setFunId(fun);
          return fe;
        }).toList();
        fun.setFormacoesFeitas(list);
      }

      if (da.getExperienciasProfssionais() != null) {
        var list = da.getExperienciasProfssionais().stream().map(e -> {
          var ee = experienciaProfissionalMapper.toEntity(e, Estado.P);
          ee.setFunId(fun);
          return ee;
        }).toList();
        fun.setExperienciasProfissionais(list);
      }
    }

    if (dto.getAnexos() != null) {
      var list = dto.getAnexos().stream().map(a -> {
        var de = documentoMapper.toEntity(a, Estado.P);
        de.setFunId(fun);
        return de;
      }).toList();
      fun.setDocumentos(list);
    }

    if (dto.getDadosBancarios() != null) {
      var list = dto.getDadosBancarios().stream().map(b -> {
        var be = dadosBancariosMapper.toEntity(b, Estado.P);
        be.setFunId(fun);
        return be;
      }).toList();
      fun.setDadosBancarios(list);
    }

    var dc = dto.getDadosContratuais();
    if (dc == null) {
      throw IgrpResponseStatusException.badRequest("Dados contratuais obrigatórios");
    }

    var contrato = contratoMapper.toContrato(dc, Estado.P);
    contrato.setFunId(fun);
    contrato.setVersao(1);
    fun.setContratos(java.util.List.of(contrato));

    var carreira = carreiraMapper.toCarreira(dc, Estado.P);
    if (carreira != null) {
      carreira.setFunId(fun);
      fun.setCarreiras(java.util.List.of(carreira));
    }

    var regime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if (regime != null) {
      regime.setFunId(fun);
      fun.setRegimesTrabalhos(java.util.List.of(regime));
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(fun);
      fun.setMobilidades(java.util.List.of(mobilidade));
    }

    if (dc.getSubsidios() != null && !dc.getSubsidios().isEmpty()) {
      var remList = dc.getSubsidios().stream()
          .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, fun, Estado.P))
          .toList();
      fun.setDefinicoesRenumeracoes(remList);
    }

    if (dc.getEncargosDescontos() != null && !dc.getEncargosDescontos().isEmpty()) {
      var pagList = dc.getEncargosDescontos().stream()
          .map(e -> defPagamentoMapper.toDefPagamento(e, fun, Estado.P))
          .toList();
      fun.setDefinicoesPagamentos(pagList);
    }

    var param = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    if(param == null){
      throw IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com nome ATIVO. " +
          "Verifique se o parametro esta cadastrado no banco de dados e tente novamente.");
    }

    var sl = contratuaisEntityMapper.toSituacaoLaboralInicial(dc, param, Estado.P);
    sl.setFunId(fun);
    fun.setSituacoesLaborais(java.util.List.of(sl));

    var tr = contratuaisEntityMapper.toRelacionamento(dc, Estado.P);
    tr.setFunId(fun);
    tr.setContratoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    //tr.setSituacLaboralId(sl);
    fun.setTiposrelacionamentos(java.util.List.of(tr));




    var valid = contratuaisEntityMapper.toValidacaoInsert("REGISTO_COLABORADOR", 1L, Estado.P); //todo resolve id later
    valid.setFunId(fun);
    valid.setTiprelId(tr);
    fun.setValidacoes(java.util.List.of(valid));

    FuncionarioEntity saved = funcionarioEntityRepository.save(fun);
    return funcionarioMapper.toResponseDTO(saved);
  }



}
