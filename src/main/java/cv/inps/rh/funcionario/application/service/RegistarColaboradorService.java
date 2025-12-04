package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

 private final ValidacaoEntityRepository validacaoEntityRepository;


  private final RemuneracaoTiprelEntityRepository renumeracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;

  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;

  @Transactional
  public FuncionarioResponseDTO saveDossierColaborador(CreateFuncionarioCommand command) {
    FuncionarioRequestDTO dto = command.getFuncionariorequest();

    var dadosPessoais = dto.getDadosPessoais();

    var dadosContratuais = dto.getDadosContratuais();

    if (funcionarioEntityRepository.existsByTipoDocumentoId_idAndNumDocumento(dadosPessoais.getTipoDocumentoId(), dadosPessoais.getNumDocumento())) {
      throw IgrpResponseStatusException.conflict( "Funcionario já registrado com esse documento");
    }

    validarDadosContratuaisService.validar(dadosContratuais);

    FuncionarioEntity fun = funcionarioMapper.toEntity(dadosPessoais, Estado.P);

    if (dto.getFamiliares() != null) {
      var list = dto.getFamiliares().stream().map(f -> {
        var fe = familiarMapper.toEntity(f, Estado.P);
        fe.setFunId(fun);
        return fe;
      }).collect(Collectors.toList());
      fun.setFamiliares(list);
    }

    if (dto.getDadosAcademicosProf() != null) {
      var da = dto.getDadosAcademicosProf();
      if (da.getHabilitacoesLiterarias() != null) {
        var list = da.getHabilitacoesLiterarias().stream().map(h -> {
          var he = habilitationLiterariaMapper.toEntity(h, Estado.P);
          he.setFunId(fun);
          return he;
        }).collect(Collectors.toList());
        fun.setHabilitacoesLiterarias(list);
      }

      if (da.getFormacoesFeitas() != null) {
        var list = da.getFormacoesFeitas().stream().map(f -> {
          var fe = formacaoFeitaMapper.toEntity(f, Estado.P);
          fe.setFunId(fun);
          return fe;
        }).collect(Collectors.toList());
        fun.setFormacoesFeitas(list);
      }

      if (da.getExperienciasProfssionais() != null) {
        var list = da.getExperienciasProfssionais().stream().map(e -> {
          var ee = experienciaProfissionalMapper.toEntity(e, Estado.P);
          ee.setFunId(fun);
          return ee;
        }).collect(Collectors.toList());
        fun.setExperienciasProfissionais(list);
      }
    }

    if (dto.getAnexos() != null) {
      var list = dto.getAnexos().stream().map(a -> {
        var de = documentoMapper.toEntity(a, Estado.P);
        de.setFunId(fun);
        return de;
      }).collect(Collectors.toList());
      fun.setDocumentos(list);
    }

    if (dto.getDadosBancarios() != null) {
      var list = dto.getDadosBancarios().stream().map(b -> {
        var be = dadosBancariosMapper.toEntity(b, Estado.P);
        be.setFunId(fun);
        return be;
      }).collect(Collectors.toList());
      fun.setDadosBancarios(list);
    }


    var contrato = contratoMapper.toContrato(dadosContratuais, Estado.P);
    contrato.setFunId(fun);
    contrato.setVersao(1);
    fun.setContratos(new ArrayList<>(List.of(contrato)));

    var carreira = carreiraMapper.toCarreira(dadosContratuais, Estado.P);
    if (carreira != null) {
      carreira.setContrVinculoId(contrato);
      contrato.setCarreiras(new ArrayList<>(List.of(carreira)));
    }

    var regime = regimeTrabalhoMapper.toRegime(dadosContratuais, Estado.P);
    if (regime != null) {
      regime.setFunId(fun);
      fun.setRegimesTrabalhos(new ArrayList<>(List.of(regime)));
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dadosContratuais, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(fun);
      fun.setMobilidades(new ArrayList<>(List.of(mobilidade)));
    }

    var tipoMovimentoSalario = tipoMovimentoHelper.getTipoMovimentoEntitySalario();
    var tipoMovimentoInps = tipoMovimentoHelper.getTipoMovimentoEntityInps();
    var tipoMovimentoIUR =  tipoMovimentoHelper.getTipoMovimentoEntityIur();

    /******************** INI RENUMERACOES ********************************/
    if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
      var remList = dadosContratuais.getSubsidios().stream()
          .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, fun, Estado.P))
          .collect(Collectors.toList());
      fun.setDefinicoesRenumeracoes(remList);
    }

    var renumeracaoSalario = definicaoRemuneracaoMapper
        .createRenumeracao(dadosContratuais.getSalario(), tipoMovimentoSalario, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), fun, dadosContratuais.getMoeda());
    var renumeracaoInps = definicaoRemuneracaoMapper
        .createRenumeracao(BigDecimal.ZERO, tipoMovimentoInps, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), fun, dadosContratuais.getMoeda());
    fun.getDefinicoesRenumeracoes().addAll(new ArrayList<>(List.of(renumeracaoSalario, renumeracaoInps)));
    /******************** FIM RENUMERACOES ********************************/


    /******************** INI PAGAMENTOS DESCONTOS ********************************/
    if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
      var pagList = dadosContratuais.getEncargosDescontos().stream()
          .map(e -> defPagamentoMapper.toDefPagamento(e, fun, Estado.P))
          .collect(Collectors.toList());
      fun.setDefinicoesPagamentos(pagList);
    }

    var pagamentoDescontoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoIUR, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), fun);
    var pagamentoDescontoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoInps, dadosContratuais.getDataInicio(), dadosContratuais.getDataFim(), fun);

    fun.getDefinicoesPagamentos().addAll(new ArrayList<>(List.of(pagamentoDescontoIUR, pagamentoDescontoINPS)));
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/


    var paramSituacaoLaboral = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    if (paramSituacaoLaboral == null) {
      throw IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com nome ATIVO.");
    }

    var situacaoLaboral = contratuaisEntityMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P);
    situacaoLaboral.setContrVinculoId(contrato);
    contrato.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tr = contratuaisEntityMapper.toRelacionamento(dadosContratuais, Estado.P);
    tr.setFunId(fun);
    tr.setContrVinculoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    tr.setSituacLaboralId(situacaoLaboral);
    fun.setTiposrelacionamentos(new ArrayList<>(List.of(tr)));


    var valid = contratuaisEntityMapper.toValidacaoInsert("INSERT", "REGISTO_COLABORADOR", Estado.P);
    valid.setFunId(fun);
    valid.setTiprelId(tr);
    fun.setValidacoes(new ArrayList<>(List.of(valid)));
    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(fun);


    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(e);
        });


    // Percorre todas as remunerações e cria RemuneracaoTiprelEntity
    List<RemuneracaoTiprelEntity> listTiprel = saved.getDefinicoesRenumeracoes().stream()
        .map(rem -> {
          RemuneracaoTiprelEntity r = new RemuneracaoTiprelEntity();
          r.setRemId(rem);
          r.setTiprelId(tr); // tr = TiposRelacionamentoEntity
          r.setUuid(UuidCreator.getTimeOrderedEpoch());
          r.setEstado(Estado.P);
          return r;
        })
        .collect(Collectors.toList());

    // Salva todas em batch
    renumeracaoTiprelEntityRepository.saveAll(listTiprel);


    // Percorre todas as definições de pagamento e cria PagTiprelEntity
    List<PagTiprelEntity> listPagTiprel = saved.getDefinicoesPagamentos().stream()
        .map(pag -> {
          PagTiprelEntity p = new PagTiprelEntity();
          p.setPagId(pag);
          p.setTiprelId(tr); // tr = TiposRelacionamentoEntity
          p.setUuid(UuidCreator.getTimeOrderedEpoch());
          p.setEstado(Estado.P);
          return p;
        })
        .collect(Collectors.toList());
    // Salva todas em batch
    pagTiprelEntityRepository.saveAll(listPagTiprel);


    return funcionarioMapper.toResponseDTO(saved);
  }


}
