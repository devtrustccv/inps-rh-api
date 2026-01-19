package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioMapper funcionarioMapper;
  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSituacaoEntityRepository paramSitLaboralEntityRepository;

  private final ValidacaoEntityRepository validacaoEntityRepository;

  private final ValidarDadosContratuaisService validarDadosContratuaisService;

  private final ParamVinculoMovimentoEntityRepository paramVinculoMovimentoEntityRepository;

  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  private final EntityManager entityManager;

  @Transactional
  public FuncionarioResponseDTO saveDossierColaborador(CreateFuncionarioCommand command) {
    FuncionarioRequestDTO dto = command.getFuncionariorequest();

    var dadosPessoais = dto.getDadosPessoais();
    var dadosContratuais = dto.getDadosContratuais();

    if (funcionarioEntityRepository.existsByTipoDocumentoId_idAndNumDocumento(dadosPessoais.getTipoDocumentoId(),
        dadosPessoais.getNumDocumento())) {
      throw IgrpResponseStatusException.conflict("Funcionario já registrado com esse documento");
    }

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

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

    CarreiraEntity carreira = null;
    if (Objects.equals(1, paramVinculo.getFlgCarreira()) && dadosContratuais.getCarreiraId() != null) {
      carreira = carreiraMapper.toCarreira(dadosContratuais, Estado.P);
      if (carreira != null) {
        carreira.setContrVinculoId(contrato);
        contrato.setCarreiras(new ArrayList<>(List.of(carreira)));
      }
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

    // verifica se vinculo tem salario
    if (Objects.equals(1, paramVinculo.getFlgSalario())) {
      /******************** INI RENUMERACOES ********************************/
      if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, fun, Estado.P))
            .collect(Collectors.toList());
        fun.setDefinicoesRenumeracoes(remList);
      }

      var vinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipo(dadosContratuais.getTipoVinculoLaboralId(),
              "REM").getFirst(); // so é associado um tipo REM SALL ao vinculo

      if (!Objects.isNull(vinculoTipoMovimentoREM)) {
        var renumeracao = definicaoRemuneracaoMapper.createRenumeracao(
            dadosContratuais.getSalario(),
            vinculoTipoMovimentoREM.getTmId(),
            dadosContratuais.getDataInicio(),
            dadosContratuais.getDataFim(),
            fun,
            dadosContratuais.getMoeda());
        fun.getDefinicoesRenumeracoes().add(renumeracao);
      }
      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, fun, Estado.P))
            .collect(Collectors.toList());
        fun.setDefinicoesPagamentos(pagList);
      }

      var listAssociacaoVinculoTipoMovimentoPag = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipo(
          dadosContratuais.getTipoVinculoLaboralId(),
          "PAG");

      if (!CollectionUtils.isEmpty(listAssociacaoVinculoTipoMovimentoPag)) {
        listAssociacaoVinculoTipoMovimentoPag.forEach(movimento -> {
          var pagamento = defPagamentoMapper.createPagamento(
              BigDecimal.ZERO,
              movimento.getTmId(),
              dadosContratuais.getDataInicio(),
              dadosContratuais.getDataFim(),
              fun);
          fun.getDefinicoesPagamentos().add(pagamento);
        });
      }
    }
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/

    var paramSituacaoLaboral = entityManager.getReference(ParamSituacaoEntity.class,dadosContratuais.getSituacaoLaboralId());

    var situacaoLaboral = dadosContratuaisMapper.toSituacaoLaboral(dadosContratuais, paramSituacaoLaboral, Estado.P,
        "INICIO",
        "NOVO_CONTRATO");
    situacaoLaboral.setContrVinculoId(contrato);
    contrato.setSituacoesLaborais(new ArrayList<>(List.of(situacaoLaboral)));

    var tr = dadosContratuaisMapper.toRelacionamento(dadosContratuais, Estado.P);
    tr.setFunId(fun);
    tr.setContrVinculoId(contrato);
    tr.setCarreiraId(carreira);
    tr.setRegimeId(regime);
    tr.setMobId(mobilidade);
    tr.setFlgProcessa(0);
    tr.setEstActAdm(1);
    tr.setSituacLaboralId(situacaoLaboral);
    fun.setTiposrelacionamentos(new ArrayList<>(List.of(tr)));

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.REGISTO_COLABORADOR.name(),
        Estado.P);
    valid.setFunId(fun);
    valid.setTiprelId(tr);
    fun.setValidacoes(new ArrayList<>(List.of(valid)));
    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(fun);

    if (!CollectionUtils.isEmpty(saved.getDefinicoesRenumeracoes())) {
      var lista = new ArrayList<TipoRelRemPagEntity>();
      for (var rem : saved.getDefinicoesRenumeracoes()) {
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tr);
        assoc.setRemId(rem);
        assoc.setPagId(null);
        lista.add(assoc);
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    if (!CollectionUtils.isEmpty(saved.getDefinicoesPagamentos())) {
      var lista = new ArrayList<TipoRelRemPagEntity>();
      for (var pag : saved.getDefinicoesPagamentos()) {
        var assoc = new TipoRelRemPagEntity();
        assoc.setTiprelId(tr);
        assoc.setPagId(pag);
        assoc.setRemId(null);
        lista.add(assoc);
      }
      tipoRelRemPagEntityRepository.saveAll(lista);
    }

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(e);
        });

    return funcionarioMapper.toResponseDTO(saved);
  }

}
