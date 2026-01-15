package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoMovimentoHelper;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoRelRemPagEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final ValidarDadosContratuaisService validarDadosContratuaisService;
  private final TipoRelRemPagEntityRepository tipoRelRemPagEntityRepository;

  @Transactional
  public Map<String, ?> validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var dadosContratuais = registroColaborador.getDadosContratuais();
    var dadosPessoaisReqDTO = registroColaborador.getDadosPessoais();

    validarDadosContratuaisService.validar(dadosContratuais);

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    if (funcionarioEntityRepository
        .existsByTipoDocumentoId_IdAndNumDocumentoAndUuidNot(dadosPessoaisReqDTO.getTipoDocumentoId(),
            dadosPessoaisReqDTO.getNumDocumento(), funcionario.getUuid())) {
      throw IgrpResponseStatusException.conflict("Funcionario já registrado com esse documento");
    }

    if (registroColaborador.getValidar() != null
        && !funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT,
            Referencia.REGISTO_COLABORADOR)) {
      throw IgrpResponseStatusException.badRequest(
          "funcionario nao tem validacao pendente de REGISTO COLABORADOR");
    }

    funcionario = funcionarioMapper.toUpdateEntity(funcionario, dadosPessoaisReqDTO);

    var contactos = contactoMapper.syncContactos(funcionario.getContactos(),
        dadosPessoaisReqDTO.getContactos());
    var familiares = familiarMapper.syncFamiliares(funcionario.getFamiliares(), registroColaborador.getFamiliares());

    var dadosAcademicosProf = registroColaborador.getDadosAcademicosProf();

    var habilitacoesLiterarias = habilitacaoLiterariaMapper.syncHabilitacoes(funcionario.getHabilitacoesLiterarias(),
        dadosAcademicosProf.getHabilitacoesLiterarias());
    var formacoesFeitas = formacaoFeitaMapper.syncFormacoes(funcionario.getFormacoesFeitas(),
        dadosAcademicosProf.getFormacoesFeitas());
    var experienciasProfissionais = experienciaProfissionalMapper.syncExperiencias(
        funcionario.getExperienciasProfissionais(), dadosAcademicosProf.getExperienciasProfssionais());

    var documentos = documentoMapper.syncDocumentos(funcionario.getDocumentos(), registroColaborador.getAnexos());
    var dadosBancarios = dadosBancariosMapper.syncBancarios(funcionario.getDadosBancarios(),
        registroColaborador.getDadosBancarios());

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

    var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(),
        dadosContratuais.getSubsidios());

    var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(),
        dadosContratuais.getEncargosDescontos());

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
        OrdemServicoEntity ordemServicoEntity = new OrdemServicoEntity();
        ordemServicoEntity.setFunId(funcionario);
        ordemServicoEntity.setTiprelId(tiposRelacionamento);
        ordemServicoEntity.setReferente(Referencia.REGISTO_COLABORADOR.name());
        ordemServicoEntity.setDescricao("Registro de colaborador");
        ordemServicoEntity.setNuOrdem("1");
        ordemServicoEntity.setEstado(Estado.A);
        funcionario.getOrdemServicos().add(ordemServicoEntity);

      }
      mudaEstado(funcionario, estado);

    }

    FuncionarioEntity saved = funcionarioEntityRepository.saveAndFlush(funcionario);

    if (saved.getDefinicoesRenumeracoes() != null && !saved.getDefinicoesRenumeracoes().isEmpty()) {
      java.util.List<TipoRelRemPagEntity> lista = new java.util.ArrayList<>();
      for (var rem : saved.getDefinicoesRenumeracoes()) {
        if (!tipoRelRemPagEntityRepository.existsByTipRelIdAndRemId(tiposRelacionamento, rem)) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTipRelId(tiposRelacionamento);
          assoc.setRemId(rem);
          assoc.setPagId(null);
          lista.add(assoc);
        }
      }
      if (!lista.isEmpty()) {
        tipoRelRemPagEntityRepository.saveAll(lista);
      }
    }

    if (saved.getDefinicoesPagamentos() != null && !saved.getDefinicoesPagamentos().isEmpty()) {
      java.util.List<TipoRelRemPagEntity> lista = new java.util.ArrayList<>();
      for (var pag : saved.getDefinicoesPagamentos()) {
        if (!tipoRelRemPagEntityRepository.existsByTipRelIdAndPagId(tiposRelacionamento, pag)) {
          var assoc = new TipoRelRemPagEntity();
          assoc.setTipRelId(tiposRelacionamento);
          assoc.setPagId(pag);
          assoc.setRemId(null);
          lista.add(assoc);
        }
      }
      if (!lista.isEmpty()) {
        tipoRelRemPagEntityRepository.saveAll(lista);
      }
    }

    return java.util.Map.of(
        "id", funcionario.getId(),
        "uuid", funcionario.getUuid());

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
        if (c != null)
          c.setEstado(estado);
      });

    var familiares = funcionarioEntity.getFamiliares();
    if (familiares != null)
      familiares.forEach(f -> {
        if (f != null)
          f.setEstado(estado);
      });

    var documentos = funcionarioEntity.getDocumentos();
    if (documentos != null)
      documentos.forEach(d -> {
        if (d != null)
          d.setEstado(estado);
      });

    var bancarios = funcionarioEntity.getDadosBancarios();
    if (bancarios != null)
      bancarios.forEach(b -> {
        if (b != null)
          b.setEstado(estado);
      });

    var habilitacoes = funcionarioEntity.getHabilitacoesLiterarias();
    if (habilitacoes != null)
      habilitacoes.forEach(h -> {
        if (h != null)
          h.setEstado(estado);
      });

    var formacoes = funcionarioEntity.getFormacoesFeitas();
    if (formacoes != null)
      formacoes.forEach(f -> {
        if (f != null)
          f.setEstado(estado);
      });

    var experiencias = funcionarioEntity.getExperienciasProfissionais();
    if (experiencias != null)
      experiencias.forEach(e -> {
        if (e != null)
          e.setEstado(estado);
      });

    var remuneracoes = funcionarioEntity.getDefinicoesRenumeracoes();
    if (remuneracoes != null)
      remuneracoes.forEach(r -> {
        if (r != null)
          r.setEstado(estado);
      });

    var pagamentos = funcionarioEntity.getDefinicoesPagamentos();
    if (pagamentos != null)
      pagamentos.forEach(p -> {
        if (p != null)
          p.setEstado(estado);
      });

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity.getUuid());
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contrato.setEstado(estado);
        contrato.getSituacoesLaborais().stream()
            .filter(o -> o.getEstado() == Estado.P)
            .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));
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
