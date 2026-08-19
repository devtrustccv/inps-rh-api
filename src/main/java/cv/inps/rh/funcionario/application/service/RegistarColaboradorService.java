package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.CreateFuncionarioCommand;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamSituacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamVinculoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

  private final TipoRelRemPagHelper tipoRelRemPagHelper;

  private final FuncionarioRules funcionarioRules;

  private final EntityManager entityManager;

  private final DocumentoEntityRepository documentoEntityRepository;

  private final ContratoHistoricoWriteService contratoHistoricoWriteService;

  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public SuccessResponseDTO saveDossierColaborador(CreateFuncionarioCommand command) {
    FuncionarioRequestDTO dto = command.getFuncionariorequest();

    var dadosPessoais = dto.getDadosPessoais();
    var dadosContratuais = dto.getDadosContratuais();

    colaboradorValidationRules.validarDadosPessoais(dadosPessoais, null);

    validarDadosContratuaisService.validar(dadosContratuais);

    var paramVinculo = entityManager.find(ParamVinculoEntity.class,
        dadosContratuais.getTipoVinculoLaboralId());

    FuncionarioEntity fun = funcionarioMapper.toEntity(dadosPessoais, Estado.P);

    if (dto.getFamiliares() != null) {
      // Impede documento de familiar repetido no agregado deste colaborador ANTES do insert, senão
      // rebenta no constraint UQ_UNIQ_FAM (nome+num_documento+fun_id) com erro cru da BD. Colaborador
      // novo, por isso não há familiares existentes a comparar (existentes = null).
      colaboradorValidationRules.verificarDuplicidadeFamiliares(dto.getFamiliares(), null);
      // Regra de negócio (impacto no subsídio de filhos): um dependente só pode ter UM colaborador
      // responsável. Se o mesmo documento já está como responsável (ativo A ou pendente P) noutro
      // colaborador, bloqueia — o P fecha a fresta de dois registos simultâneos por validar.
      colaboradorValidationRules.verificarResponsavelUnicoAgregado(dto.getFamiliares(), fun.getUuid());
      var list = dto.getFamiliares().stream().map(f -> {
        var fe = familiarMapper.toEntity(f, Estado.P, fun);
        return fe;
      }).collect(Collectors.toList());
      fun.setFamiliares(list);
    }

    if (dto.getDadosAcademicosProf() != null) {
      var da = dto.getDadosAcademicosProf();
      if (da.getHabilitacoesLiterarias() != null) {
        colaboradorValidationRules.validarHabilitacoesLiterarias(da.getHabilitacoesLiterarias());
        var list = da.getHabilitacoesLiterarias().stream().map(h -> {
          var he = habilitationLiterariaMapper.toEntity(h, Estado.P, fun);
          return he;
        }).collect(Collectors.toList());
        fun.setHabilitacoesLiterarias(list);
      }

      if (da.getFormacoesFeitas() != null) {
        var list = da.getFormacoesFeitas().stream().map(f -> {
          var fe = formacaoFeitaMapper.toEntity(f, Estado.P, fun);
          return fe;
        }).collect(Collectors.toList());
        fun.setFormacoesFeitas(list);
      }

      if (da.getExperienciasProfssionais() != null) {
        var list = da.getExperienciasProfssionais().stream().map(e -> {
          var ee = experienciaProfissionalMapper.toEntity(e, Estado.P, fun);
          return ee;
        }).collect(Collectors.toList());
        fun.setExperienciasProfissionais(list);
      }
    }


    // NIB é obrigatório em cada registo bancário quando o vínculo tem salário (flgSalario=1)
    colaboradorValidationRules.validarNibObrigatorioSeSalario(
        dadosContratuais.getTipoVinculoLaboralId(), dto.getDadosBancarios());

    if (dto.getDadosBancarios() != null) {
      var list = dto.getDadosBancarios().stream().map(b -> {
        var be = dadosBancariosMapper.toEntity(b, Estado.P, fun);
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
      colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());

      if (dadosContratuais.getSubsidios() != null && !dadosContratuais.getSubsidios().isEmpty()) {
        var remList = dadosContratuais.getSubsidios().stream()
            .map(s -> definicaoRemuneracaoMapper.toDefinicaoRemuneracao(s, fun, Estado.P))
            .collect(Collectors.toList());
        fun.setDefinicoesRenumeracoes(remList);
      }

      var listVinculoTipoMovimentoREM = paramVinculoMovimentoEntityRepository
          .findByVinculoId_IdAndTipoAndEstado(dadosContratuais.getTipoVinculoLaboralId(), "REM", Estado.A);

      if (CollectionUtils.isEmpty(listVinculoTipoMovimentoREM)) {
        throw IgrpResponseStatusException.badRequest(
            "O tipo de vínculo selecionado não tem tipo de movimento salarial (REM) parametrizado. " +
            "Parametrize o vínculo antes de registar o colaborador.");
      }

      // NOTA: derivacao do salario (REM) do vinculo movida para a validacao positiva
      // (ReconciliacaoMovimentoVinculoService, via ValidarRegistoColaboradorService).
      // Mantem-se acima o fail-fast (vinculo tem de ter REM parametrizado) e os extras do utilizador.
      /******************** FIM RENUMERACOES ********************************/

      /******************** INI PAGAMENTOS DESCONTOS ********************************/
      colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

      if (dadosContratuais.getEncargosDescontos() != null && !dadosContratuais.getEncargosDescontos().isEmpty()) {
        var pagList = dadosContratuais.getEncargosDescontos().stream()
            .map(e -> defPagamentoMapper.toDefPagamento(e, fun, Estado.P))
            .collect(Collectors.toList());
        fun.setDefinicoesPagamentos(pagList);
      }

      var listAssociacaoVinculoTipoMovimentoPag = paramVinculoMovimentoEntityRepository.findByVinculoId_IdAndTipoAndEstado(
          dadosContratuais.getTipoVinculoLaboralId(),
          "PAG",
          Estado.A);

      if (CollectionUtils.isEmpty(listAssociacaoVinculoTipoMovimentoPag)) {
        throw IgrpResponseStatusException.badRequest(
            "O tipo de vínculo selecionado não tem tipos de movimento de pagamento (PAG) parametrizados. " +
            "Parametrize o vínculo antes de registar o colaborador.");
      }

      // NOTA: derivacao dos PAG do vinculo movida para a validacao positiva
      // (ReconciliacaoMovimentoVinculoService, via ValidarRegistoColaboradorService).
      // Mantem-se acima o fail-fast (vinculo tem de ter PAG parametrizado) e os extras do utilizador.
    }
    /******************** FIM PAGAMENTOS DESCONTOS ********************************/

    var paramSituacaoLaboral = ValidationUtil.ref(entityManager, ParamSituacaoEntity.class,
        dadosContratuais.getSituacaoLaboralId());

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
    tr.setFlgProcessa(1);
    tr.setEstActAdm(1);
    tr.setSituacLaboralId(situacaoLaboral);

    // Registo do colaborador: TIPO_SITUACAO = "INICIO" em todas as tabelas (caso de teste).
    tr.setTipoSituacao("INICIO");
    if (carreira != null) carreira.setTipoSituacao("INICIO");
    if (regime != null) regime.setTipoSituacao("INICIO");
    if (mobilidade != null) mobilidade.setTipoSituacao("INICIO");

    fun.setTiposrelacionamentos(new ArrayList<>(List.of(tr)));
    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.REGISTO_COLABORADOR.name(),
        Estado.P);
    valid.setFunId(fun);
    valid.setTiprelId(tr);
    valid.setReferenciaUuid(fun.getUuid());
    fun.setValidacoes(new ArrayList<>(List.of(valid)));

    var alertas = funcionarioRules.validarContactosDuplicados(dadosPessoais.getContactos(), null);

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(fun);

    contratoHistoricoWriteService.registrarNovo(contrato);

    if (dto.getAnexos() != null) {
      var list = dto.getAnexos().stream().map(a -> documentoMapper.toEntity(
          a,
          Estado.P,
          TableName.RH_T_FUNCIONARIOS.name(),
          saved.getId(),
          saved.getUuid(),
          1L,
          saved)).collect(Collectors.toList());
      documentoEntityRepository.saveAll(list);
    }

    tipoRelRemPagHelper.associarNovos(tr, saved);

    validacaoEntityRepository.findById(valid.getId())
        .ifPresent(e -> {
          e.setReferenciaId(saved.getId());
          e.setReferenciaUuid(saved.getUuid());
          validacaoEntityRepository.save(e);
        });

    return new SuccessResponseDTO(true, saved.getUuid().toString(), "Colaborador registado com sucesso.", alertas);

  }

}
