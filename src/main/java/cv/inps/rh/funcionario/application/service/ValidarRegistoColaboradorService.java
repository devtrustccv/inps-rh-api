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
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidarRegistoColaboradorService {

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
  private final TipoMovimentoHelper tipoMovimentoHelper;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final OrdemServicoWriteService ordemServicoWriteService;
  private final ContratoHistoricoWriteService contratoHistoricoWriteService;
  private final ColaboradorValidationRules colaboradorValidationRules;
  private final ReconciliacaoMovimentoVinculoService reconciliacaoMovimentoVinculoService;

  @Transactional
  public Map<String, ?> validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var dadosContratuais = registroColaborador.getDadosContratuais();
    var dadosPessoaisReqDTO = registroColaborador.getDadosPessoais();

    validarDadosContratuaisService.validar(dadosContratuais);

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);
    funcionarioRules.garantirEditavel(funcionario.getEstado());

    colaboradorValidationRules.validarDadosPessoais(dadosPessoaisReqDTO, funcionario.getUuid());

    if (registroColaborador.getValidar() != null
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

    var dadosAcademicosProf = registroColaborador.getDadosAcademicosProf();

    colaboradorValidationRules.validarHabilitacoesLiterarias(dadosAcademicosProf.getHabilitacoesLiterarias());

    var habilitacoesLiterarias = habilitacaoLiterariaMapper
        .syncHabilitacoes(funcionario.getHabilitacoesLiterarias(),
        dadosAcademicosProf.getHabilitacoesLiterarias(), funcionario, Estado.P);

    var formacoesFeitas = formacaoFeitaMapper
        .syncFormacoes(funcionario.getFormacoesFeitas(),
        dadosAcademicosProf.getFormacoesFeitas(), funcionario, Estado.P);

    var experienciasProfissionais = experienciaProfissionalMapper
        .syncExperiencias(
        funcionario.getExperienciasProfissionais(),
        dadosAcademicosProf.getExperienciasProfssionais(), funcionario, Estado.P);

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

    var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
        dadosContratuais.getSubsidios(), funcionario);

    var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
        dadosContratuais.getEncargosDescontos(), funcionario);

    var alertas = funcionarioRules.validarContactosDuplicados(dadosPessoaisReqDTO.getContactos(), funcionario.getUuid());

    funcionario.setContactos(contactos);
    funcionario.setFamiliares(familiares);
    funcionario.setDocumentos(documentos);
    funcionario.setDadosBancarios(dadosBancarios);
    funcionario.getDefinicoesRenumeracoes().addAll(definicoesRemuneracoes);
    funcionario.getDefinicoesPagamentos().addAll(definicoesPagamentos);
    funcionario.setHabilitacoesLiterarias(habilitacoesLiterarias);
    funcionario.setFormacoesFeitas(formacoesFeitas);
    funcionario.setExperienciasProfissionais(experienciasProfissionais);

    if (registroColaborador.getValidar() != null) {
      var estado = registroColaborador.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
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

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    tipoRelRemPagHelper.associarNovos(tiposRelacionamento, saved);

    var result = new java.util.HashMap<String, Object>();
    result.put("id", funcionario.getId());
    result.put("uuid", funcionario.getUuid());
    result.put("alertas", alertas);
    return result;

  }

  private void mudaEstado(FuncionarioEntity funcionarioEntity, Estado estado) {
    if (funcionarioEntity == null)
      return;
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

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.INSERT, Referencia.REGISTO_COLABORADOR)
        .ifPresent(v -> v.setEstado(estado));

  }

}
