package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;

  private final RemuneracaoTiprelEntityRepository renumeracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;

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

    var dc = dto.getDadosContratuais();
    if (dc == null) {
      throw IgrpResponseStatusException.badRequest("Dados contratuais obrigatórios");
    }

    var contrato = contratoMapper.toContrato(dc, Estado.P);
    contrato.setFunId(fun);
    contrato.setVersao(1);
    fun.setContratos(new ArrayList<>(List.of(contrato)));

    var carreira = carreiraMapper.toCarreira(dc, Estado.P);
    if (carreira != null) {
      carreira.setFunId(fun);
      fun.setCarreiras(new ArrayList<>(List.of(carreira)));
    }

    var regime = regimeTrabalhoMapper.toRegime(dc, Estado.P);
    if (regime != null) {
      regime.setFunId(fun);
      fun.setRegimesTrabalhos(new ArrayList<>(List.of(regime)));
    }

    var mobilidade = mobilidadeMapper.toMobilidade(dc, Estado.P);
    if (mobilidade != null) {
      mobilidade.setFunId(fun);
      fun.setMobilidades(new ArrayList<>(List.of(mobilidade)));
    }


    var tipoMovimentoSalario = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("SALL", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento SALARIO nao encontrado."));

    var tipoMovimentoInps = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("INPS", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento INPS nao encontrado."));

    var tipoMovimentoIUR = tipoMovimentoEntityRepository.findByShortDescAndAmbAplId("IUR", 30L)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Tipo de movimento IUR nao encontrado."));

    /***********************RENUMERACOES ********************************/
    if (dc.getSubsidios() != null && !dc.getSubsidios().isEmpty()) {
      var remList = dc.getSubsidios().stream()
          .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, fun, Estado.P))
          .collect(Collectors.toList());
      fun.setDefinicoesRenumeracoes(remList);
    }

    var renumeracaoSalario = definicaoRemuneracaoMapper
        .createRenumeracao(dc.getSalario(), tipoMovimentoSalario, dc.getDataInicio(), dc.getDataFim(), fun);
    var renumeracaoInps = definicaoRemuneracaoMapper
        .createRenumeracao(BigDecimal.ZERO, tipoMovimentoInps, dc.getDataInicio(), dc.getDataFim(), fun);
    fun.getDefinicoesRenumeracoes().addAll(new ArrayList<>(List.of(renumeracaoSalario, renumeracaoInps)));


    /***********************PAGAMENTOS DESCONTOS ********************************/
    if (dc.getEncargosDescontos() != null && !dc.getEncargosDescontos().isEmpty()) {
      var pagList = dc.getEncargosDescontos().stream()
          .map(e -> defPagamentoMapper.toDefPagamento(e, fun, Estado.P))
          .collect(Collectors.toList());
      fun.setDefinicoesPagamentos(pagList);
    }

    var pagamentoDescontoIUR = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoIUR, dc.getDataInicio(), dc.getDataFim(), fun);
    var pagamentoDescontoINPS = defPagamentoMapper.createPagamento(BigDecimal.ZERO,
        tipoMovimentoInps, dc.getDataInicio(), dc.getDataFim(), fun);

    fun.getDefinicoesPagamentos().addAll(new ArrayList<>(List.of(pagamentoDescontoIUR, pagamentoDescontoINPS)));


    var param = paramSitLaboralEntityRepository.findAllByNome("ATIVO").getFirst();
    if (param == null) {
      throw IgrpResponseStatusException.notFound("Parametro de situacao laboral nao encontrado com nome ATIVO.");
    }

    var sl = contratuaisEntityMapper.toSituacaoLaboralInicial(dc, param, Estado.P);
    sl.setFunId(fun);
    fun.setSituacoesLaborais(new ArrayList<>(List.of(sl)));

    var tr = contratuaisEntityMapper.toRelacionamento(dc, Estado.P);
    tr.setFunId(fun);
    tr.setContratoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa("NAO");
    tr.setEstActAdm(1);
    //tr.setSituacLaboralId(sl);
    fun.setTiposrelacionamentos(new ArrayList<>(List.of(tr)));


    var valid = contratuaisEntityMapper.toValidacaoInsert("INSERT", "REGISTO_COLABORADOR", Estado.P); //todo resolve id later
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


  private void validarDadosContratuais(DadosContratuaisReqDTO dc) {

    // -----------------------------
    // OBRIGATÓRIOS BÁSICOS
    // -----------------------------
    if (dc.getTipoContratoId() == null)
      throw IgrpResponseStatusException.badRequest("Tipo de contrato é obrigatório.");

    if (dc.getCargoPosicaoId() == null)
      throw IgrpResponseStatusException.badRequest("Cargo/posição é obrigatório.");

    if (dc.getDirecaoId() == null)
      throw IgrpResponseStatusException.badRequest("Direção é obrigatória.");

    if (dc.getSeccaoId() == null)
      throw IgrpResponseStatusException.badRequest("Seção é obrigatória.");

    if (dc.getLocalTrabalhoId() == null)
      throw IgrpResponseStatusException.badRequest("Local de trabalho é obrigatório.");

    if (dc.getPaisId() == null)
      throw IgrpResponseStatusException.badRequest("País é obrigatório.");

    if (dc.getIlhaId() == null)
      throw IgrpResponseStatusException.badRequest("Ilha é obrigatória.");

    if (dc.getMoeda() == null || dc.getMoeda().isBlank())
      throw IgrpResponseStatusException.badRequest("Moeda é obrigatória.");

    if (dc.getDataInicio() == null)
      throw IgrpResponseStatusException.badRequest("Data de início é obrigatória.");


    // -----------------------------
    // REGRAS DE DATAS
    // -----------------------------
    var hoje = LocalDate.now();

    if (dc.getDataInicio().isAfter(hoje))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser maior que a data atual.");

    if (dc.getDataFim() != null && dc.getDataInicio().isAfter(dc.getDataFim()))
      throw IgrpResponseStatusException.badRequest("Data início não pode ser superior à data fim.");


    // -----------------------------
    // OBRIGATÓRIOS POR TIPO DE VÍNCULO
    // -----------------------------
    var vinculo = entityManager.getReference(ParamVinculoEntity.class, dc.getTipoVinculoLaboralId());

    // flgCarreira = 1 → carreira, categoria, escalão obrigatórios
    if (vinculo.getFlgCarreira() != null && vinculo.getFlgCarreira() == 1) {

      if (dc.getCarreiraId() == null)
        throw IgrpResponseStatusException.badRequest("Carreira é obrigatória para este tipo de vínculo.");

      if (dc.getCategoriaId() == null)
        throw IgrpResponseStatusException.badRequest("Categoria é obrigatória para este tipo de vínculo.");

      if (dc.getEscalaoReferenciaId() == null)
        throw IgrpResponseStatusException.badRequest("Escalão é obrigatório para este tipo de vínculo.");

      // Salário automático
      var escalao = entityManager.getReference(ParamEscalaoEntity.class, dc.getEscalaoReferenciaId());
      dc.setSalario(escalao.getValor());
    }

    // flgSalario = 1 → salário é obrigatório
    if (vinculo.getFlgSalario() != null && vinculo.getFlgSalario() == 1) {
      if (dc.getSalario() == null)
        throw IgrpResponseStatusException.badRequest("Salário é obrigatório para este tipo de vínculo.");
    }
  }


}
