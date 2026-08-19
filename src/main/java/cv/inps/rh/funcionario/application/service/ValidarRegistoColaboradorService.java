package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidarRegistoColaboradorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValidarRegistoColaboradorService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final ContactoMapper contactoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ColaboradorValidationRules colaboradorValidationRules;
  private final ReconciliacaoMovimentoVinculoService reconciliacaoMovimentoVinculoService;

  @Transactional
  public SuccessResponseDTO validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var validar = registroColaborador.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);
    funcionarioRules.garantirEditavel(funcionario.getEstado());

    // Fluxo maker-checker do registo de colaborador, governado pelo ESTADO do registo (não por
    // flags do cliente):
    //   - registo em P -> o checker decide: SIM (activa), NAO (rejeita) ou CORRIGIR (devolve).
    //   - registo em C -> o maker corrige e reenvia (C -> P); 'validar' tem de vir nulo.

    // Checker devolve para correção: P -> C, SEM aplicar o payload (o checker não edita).
    if (EstadoValidacao.CORRIGIR.equals(validar)) {
      if (funcionario.getEstado() != Estado.P
          || !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
              Referencia.REGISTO_COLABORADOR)) {
        throw IgrpResponseStatusException.badRequest(
            "Só é possível devolver para correção um registo de colaborador pendente de validação.");
      }
      mudaEstado(funcionario, Estado.C);
      funcionarioEntityRepository.saveAndFlush(funcionario);
      return new SuccessResponseDTO(true, funcionario.getUuid().toString(),
          "Registo de colaborador devolvido para correção.", List.of());
    }

    // A partir daqui aplica-se o payload. Se o registo está em C, é o maker a corrigir (C -> P):
    // 'validar' não pode vir preenchido e tem de existir realmente uma validação por corrigir.
    boolean estaPorCorrigir = funcionario.getEstado() == Estado.C;
    if (estaPorCorrigir) {
      if (validar != null) {
        throw IgrpResponseStatusException.badRequest(
            "Registo em correção: não pode ser validado. Corrija e reenvie primeiro.");
      }
      if (!funcionarioRules.temValidacaoPorCorrigir(funcionario.getUuid(), TipoAcao.INSERT,
          Referencia.REGISTO_COLABORADOR)) {
        throw IgrpResponseStatusException.badRequest("Este registo de colaborador não está por corrigir.");
      }
      if (registroColaborador.getDadosPessoais() == null || registroColaborador.getDadosContratuais() == null) {
        throw IgrpResponseStatusException.badRequest(
            "Correção sem dados: reenvie o formulário completo do colaborador.");
      }
    }

    var dadosContratuais = registroColaborador.getDadosContratuais();
    var dadosPessoaisReqDTO = registroColaborador.getDadosPessoais();

    validarDadosContratuaisService.validar(dadosContratuais);

    colaboradorValidationRules.validarDadosPessoais(dadosPessoaisReqDTO, funcionario.getUuid());

    if (validar != null
        && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
            Referencia.REGISTO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente de REGISTO COLABORADOR");
    }

    funcionario = funcionarioMapper.toUpdateEntity(funcionario, dadosPessoaisReqDTO);

    var contactos = contactoMapper.syncContactos(funcionario.getContactos(),
        dadosPessoaisReqDTO.getContactos(), funcionario);

    colaboradorValidationRules.verificarDuplicidadeFamiliares(
        registroColaborador.getFamiliares(), funcionario.getFamiliares());

    var familiares = familiarMapper
        .syncFamiliares(funcionario.getFamiliares(),
            registroColaborador.getFamiliares(), funcionario, Estado.P);

    // dadosAcademicosProf pode vir null (secção não enviada no payload). Nesse caso não se mexe
    // nas coleções académicas: evita o NPE e evita o soft-delete indevido das existentes (sync
    // com lista vazia apagaria; aqui mantemos as atuais).
    var dadosAcademicosProf = registroColaborador.getDadosAcademicosProf();
    var habilitacoesLiterarias = funcionario.getHabilitacoesLiterarias();
    var formacoesFeitas = funcionario.getFormacoesFeitas();
    var experienciasProfissionais = funcionario.getExperienciasProfissionais();
    if (dadosAcademicosProf != null) {
      colaboradorValidationRules.validarHabilitacoesLiterarias(dadosAcademicosProf.getHabilitacoesLiterarias());
      habilitacoesLiterarias = habilitacaoLiterariaMapper.syncHabilitacoes(
          funcionario.getHabilitacoesLiterarias(), dadosAcademicosProf.getHabilitacoesLiterarias(), funcionario, Estado.P);
      formacoesFeitas = formacaoFeitaMapper.syncFormacoes(
          funcionario.getFormacoesFeitas(), dadosAcademicosProf.getFormacoesFeitas(), funcionario, Estado.P);
      experienciasProfissionais = experienciaProfissionalMapper.syncExperiencias(
          funcionario.getExperienciasProfissionais(), dadosAcademicosProf.getExperienciasProfssionais(), funcionario, Estado.P);
    }

    // NIB é obrigatório em cada registo bancário quando o vínculo tem salário (flgSalario=1)
    colaboradorValidationRules.validarNibObrigatorioSeSalario(
        dadosContratuais.getTipoVinculoLaboralId(), registroColaborador.getDadosBancarios());

    var dadosBancarios = dadosBancariosMapper
        .syncBancarios(funcionario.getDadosBancarios(),
        registroColaborador.getDadosBancarios(), funcionario);

    var documentos = documentoMapper.syncDocumentos(
        funcionario.getDocumentos(),
        registroColaborador.getAnexos(),
        TableName.RH_T_FUNCIONARIOS.name(),
        funcionario.getId(),
        funcionario.getUuid(),
        1L,
        funcionario);


    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dadosContratuais);

    var situacaoLaboral = tiposRelacionamento.getSituacLaboralId();
    dadosContratuaisMapper.toUpdateSituacaoLaboral(situacaoLaboral, dadosContratuais);

    var contrato = tiposRelacionamento.getContrVinculoId();
    contratoMapper.toUpdateEntity(contrato, dadosContratuais);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dadosContratuais);

    var carreira = tiposRelacionamento.getCarreiraId();
    carreiraMapper.toUpdateEntity(carreira, dadosContratuais);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dadosContratuais);

    // Registo do colaborador: TIPO_SITUACAO = "INICIO" em todas as tabelas afetadas (caso de teste).
    // Reforça aqui porque os toUpdate* repõem "NOVO_CONTRATO"; a validação não deve alterar o
    // tipo de situação definido no registo.
    tiposRelacionamento.setTipoSituacao("INICIO");
    if (carreira != null) carreira.setTipoSituacao("INICIO");
    if (regime != null) regime.setTipoSituacao("INICIO");
    if (mobilidade != null) mobilidade.setTipoSituacao("INICIO");

    colaboradorValidationRules.validarSubsidiosDuplicados(dadosContratuais.getSubsidios());
    colaboradorValidationRules.validarEncargosDescontosDuplicados(dadosContratuais.getEncargosDescontos());

    // tms dos movimentos FIXOS do vinculo — o sync so mexe nos manuais, protege os fixos.
    var vinculoId = dadosContratuais.getTipoVinculoLaboralId();
    var tmsFixosRem = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "REM");
    var tmsFixosPag = reconciliacaoMovimentoVinculoService.tmsFixosDoVinculo(vinculoId, "PAG");
    var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
        dadosContratuais.getSubsidios(), funcionario, tmsFixosRem);

    var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
        dadosContratuais.getEncargosDescontos(), funcionario, tmsFixosPag);

    var alertas = funcionarioRules.validarContactosDuplicados(dadosPessoaisReqDTO.getContactos(), funcionario.getUuid());

    funcionario.setContactos(contactos);
    funcionario.setFamiliares(familiares);
    funcionario.setDocumentos(documentos);
    funcionario.setDadosBancarios(dadosBancarios);
    funcionario.getDefinicoesRenumeracoes().addAll(definicoesRemuneracoes);
    funcionario.getDefinicoesPagamentos().addAll(definicoesPagamentos);

    // Subsidios (def_remuneracoes) nao tem datas proprias no formulario -> seguem o periodo do
    // contrato (antes ficavam com NOW). Os encargos ja recebem o default do contrato no
    // ValidarDadosContratuaisService quando o utilizador nao indica.
    var contratoRl = tiposRelacionamento.getContrVinculoId();
    if (contratoRl != null) {
      var ci = contratoRl.getDataInicio();
      var cf = contratoRl.getDataFim();
      funcionario.getDefinicoesRenumeracoes().forEach(r -> {
        if (r != null && r.getEstado() != Estado.E) {
          r.setDataInicio(ci); r.setDataFim(cf);
          // Subsidio (SubsidioReqDTO nao tem moeda) -> herda a moeda do contrato (ex.: CVE).
          if (r.getMoeda() == null) r.setMoeda(dadosContratuais.getMoeda());
        }
      });
    }

    funcionario.setHabilitacoesLiterarias(habilitacoesLiterarias);
    funcionario.setFormacoesFeitas(formacoesFeitas);
    funcionario.setExperienciasProfissionais(experienciasProfissionais);

    if (estaPorCorrigir) {
      // Correção reenviada pelo maker: C -> P (volta à fila de validação). Sem reconciliar nem
      // ordem de serviço — isso só acontece na validação positiva (SIM).
      mudaEstado(funcionario, Estado.P);
    } else if (validar != null) {
      var estado = EstadoValidacao.SIM.equals(validar) ? Estado.A : Estado.I;
      if (estado.equals(Estado.A)) {
        // derivar os movimentos fixos do vinculo — SO na validacao positiva (antes de activar)
        reconciliacaoMovimentoVinculoService.reconciliar(funcionario, tiposRelacionamento.getContrVinculoId(),
            dadosContratuais.getSalario(), dadosContratuais.getMoeda(),
            dadosContratuais.getDataInicio(), dadosContratuais.getDataFim());
        var validacao = funcionarioRules.getValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.REGISTO_COLABORADOR).orElse(null);
        var descricao = "Registo de colaborador - " + funcionario.getNome();
        ordemServicoWriteService.criar(funcionario, tiposRelacionamento, registroColaborador.getTipoOrdemServico(), validacao, descricao);
      }
      mudaEstado(funcionario, estado);
    }

    // Registo do colaborador: marca TODOS os def (remuneracoes + pagamentos) com obs="INICIO",
    // identificando-os como a versao INICIAL do contrato. Feito AQUI, depois do reconciliar (que
    // acrescenta o salario-base + PAG fixos as colecoes), para apanhar tambem esses — nao so os
    // subsidios manuais. Sobrescreve qualquer obs do formulario. Ignora os E (eliminados).
    funcionario.getDefinicoesRenumeracoes().forEach(r -> {
      if (r != null && r.getEstado() != Estado.E) r.setObs("INICIO");
    });
    funcionario.getDefinicoesPagamentos().forEach(p -> {
      if (p != null && p.getEstado() != Estado.E) p.setObs("INICIO");
    });

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    // Numa REJEICAO (validar=NAO) nao se associam defs ao tiprel rejeitado — RH_T_TIPREL_REM_PAG
    // nao tem estado, logo a unica forma de nao os ter e nao criar a associacao.
    if (!EstadoValidacao.NAO.equals(registroColaborador.getValidar())) {
      tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);
    }

    var mensagem = estaPorCorrigir
        ? "Registo de colaborador corrigido e reenviado para validação."
        : EstadoValidacao.SIM.equals(validar)
            ? "Registo de colaborador validado."
            : "Registo de colaborador actualizado.";
    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), mensagem, alertas);

  }

  private void mudaEstado(FuncionarioEntity funcionarioEntity, Estado estado) {
    if (funcionarioEntity == null)
      return;
    // Origem = estado in-flight actual do registo (P quando o checker decide, C quando o maker
    // reenvia a correção). Capturada ANTES de mutar; o grafo move-se todo em lockstep.
    var estadoOrigem = funcionarioEntity.getEstado();
    funcionarioEntity.setEstado(estado);
    funcionarioEntity.setEstadoValidacao(estado != null ? estado.name() : null);

    var documentoPessoal = funcionarioEntity.getDocumentoPessoal();
    if (documentoPessoal != null)
      documentoPessoal.setEstado(estado);

    var endereco = funcionarioEntity.getEndereco();
    if (endereco != null)
      endereco.setEstado(estado);

    var contactos = funcionarioEntity.getContactos();
    if (contactos != null)
      contactos.forEach(c -> {
        if (c != null && c.getEstado() != Estado.E)
          c.setEstado(estado);
      });

    var familiares = funcionarioEntity.getFamiliares();
    if (familiares != null)
      familiares.forEach(f -> {
        if (f != null && f.getEstado() != Estado.E)
          f.setEstado(estado);
      });

    var documentos = funcionarioEntity.getDocumentos();
    if (documentos != null)
      documentos.forEach(d -> {
        if (d != null && d.getEstado() != Estado.E)
          d.setEstado(estado);
      });

    var bancarios = funcionarioEntity.getDadosBancarios();
    if (bancarios != null)
      bancarios.forEach(b -> {
        if (b != null && b.getEstado() != Estado.E)
          b.setEstado(estado);
      });

    var habilitacoes = funcionarioEntity.getHabilitacoesLiterarias();
    if (habilitacoes != null)
      habilitacoes.forEach(h -> {
        if (h != null && h.getEstado() != Estado.E)
          h.setEstado(estado);
      });

    var formacoes = funcionarioEntity.getFormacoesFeitas();
    if (formacoes != null)
      formacoes.forEach(f -> {
        if (f != null && f.getEstado() != Estado.E)
          f.setEstado(estado);
      });

    var experiencias = funcionarioEntity.getExperienciasProfissionais();
    if (experiencias != null)
      experiencias.forEach(e -> {
        if (e != null && e.getEstado() != Estado.E)
          e.setEstado(estado);
      });

    var remuneracoes = funcionarioEntity.getDefinicoesRenumeracoes();
    if (remuneracoes != null)
      remuneracoes.forEach(r -> {
        if (r != null && r.getEstado() != Estado.E)
          r.setEstado(estado);
      });

    var pagamentos = funcionarioEntity.getDefinicoesPagamentos();
    if (pagamentos != null)
      pagamentos.forEach(p -> {
        if (p != null && p.getEstado() != Estado.E)
          p.setEstado(estado);
      });

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contratoHistoricoWriteService.transicionarEstado(contrato, estado);
      }

      var mob = tr.getMobId();
      if (mob != null)
        mob.setEstado(estado);

      var carreira = tr.getCarreiraId();
      if (carreira != null)
        carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null)
        regime.setEstado(estado);

      var situacaoLaboral = tr.getSituacLaboralId();
      if (situacaoLaboral != null)
        situacaoLaboral.setEstado(estado);
    }

    funcionarioRules.getValidacao(funcionarioEntity.getUuid(), estadoOrigem, TipoAcao.INSERT, Referencia.REGISTO_COLABORADOR)
        .ifPresent(v -> v.setEstado(estado));

  }

}
