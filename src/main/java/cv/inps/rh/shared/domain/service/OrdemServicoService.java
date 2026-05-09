package cv.inps.rh.shared.domain.service;

import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.ReportHtmlDTO;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVRelacaoLaboralEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrdemServicoService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules rules;
  private final ParamDocOutputEntityRepository paramDocOutputEntityRepository;
  private final RhVRelacaoLaboralEntityRepository rhVRelacaoLaboralEntityRepository;

  public Context fimComissaoServico(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.FIM_COMISSAO_SERVICO);
  }

  public ReportHtmlDTO getFimComissaoServicoContent(String funcionarioId) {
    var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var currentContract = rules.getContratoComMaiorVersao(fun.getUuid());
    var documentOutputType = getByDocType(OrdemServico.FIM_COMISSAO_SERVICO.name());
    var values = Map.of(
        "nomeColaborador", fun.getNome(),
        "cargoColaborador", currentContract.getVinculoId().getNome(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(currentContract.getDataInicio())
    );
    return new ReportHtmlDTO(StringSubstitutor.replace(documentOutputType.getCorpo(), values));
  }

  public Context conversaoContrato(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.CONVERSAO_CONTRATO);
  }

  public ReportHtmlDTO conversaoContratoContent(String funcionarioId) {

    var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var documentOutputType = getByDocType(OrdemServico.CONVERSAO_CONTRATO.name());
    var values = Map.of(
        "nomeColaborador", fun.getNome(),
        "dataConversaoContrato", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()),
        "dataReuniao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );
    return new ReportHtmlDTO(StringSubstitutor.replace(documentOutputType.getCorpo(), values));
  }

  public Context licensaSemVencimento(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.LICENSA_SEM_VENCIMENTO);
  }

  public ReportHtmlDTO licensaSemVencimentoContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);
    var documentOutputType = getByDocType(OrdemServico.LICENSA_SEM_VENCIMENTO.name());
    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "periodo", Period.between(fun.getDataInicioSituacao(), fun.getDataFimSituacao()).getMonths(),
        "cargo", fun.getCargoDesc(),
        "categoria", fun.getEscalaoDesc(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );
    return new ReportHtmlDTO(StringSubstitutor.replace(documentOutputType.getCorpo(), values));
  }

  public Context progressaoCargo(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.PROGRESSAO_CARGO);
  }

  public ReportHtmlDTO progressaoCargoContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.PROGRESSAO_CARGO.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalaoAtual", fun.getEscalaoDesc(),
        "novoEscalao", "14D",
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context progressaoCategoria(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.PROGRESSAO_CATEGORIA);
  }

  public ReportHtmlDTO progressaoCategoriaContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.PROGRESSAO_CATEGORIA.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "categoriaAtual", fun.getEscalaoDesc(),
        "novaCategoria", "4B",
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context substituicao(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.SUBSTITUICAO);
  }

  public ReportHtmlDTO substituicaoContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.SUBSTITUICAO.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "motivo", "férias",
        "dataInicio", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()),
        "dataFim", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now().plusDays(15))
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context transferencia(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.TRANSFERENCIA);
  }

  public ReportHtmlDTO transferenciaContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.TRANSFERENCIA.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "seccao", fun.getSeccaoDesc(),
        "ilha", fun.getLocalTrabIlha(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context mobilidadeInterna(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.MOBILIDADE_INTERNA);
  }

  public ReportHtmlDTO mobilidadeInternaContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.MOBILIDADE_INTERNA.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "seccao", fun.getSeccaoDesc()
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context requalificacao(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.REQUALIFICACAO);
  }

  public ReportHtmlDTO requalificacaoContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.REQUALIFICACAO.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargoAtual", fun.getCargoDesc(),
        "escalaoAtual", fun.getEscalaoDesc(),
        "novoCargo", "Técnico Superior",
        "novoEscalao", "10F"
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  public Context nomeacaoCoordenador(String htmlBody) {
    return buildContext(htmlBody, OrdemServico.NOMEACAO_COORDENADOR);
  }

  public ReportHtmlDTO nomeacaoCoordenadorContent(String funcionarioId) {

    var fun = rhVRelacaoLaboralEntityRepository.findByIdOrThrow(funcionarioId);

    var documentOutputType = getByDocType(OrdemServico.NOMEACAO_COORDENADOR.name());

    var values = Map.of(
        "nomeColaborador", fun.getNomeColaborador(),
        "cargo", fun.getCargoDesc(),
        "escalao", fun.getEscalaoDesc(),
        "direcao", fun.getDirecaoDesc(),
        "dataEfeito", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now())
    );

    return new ReportHtmlDTO(
        StringSubstitutor.replace(documentOutputType.getCorpo(), values)
    );
  }

  private Context buildContext(String htmlBody, OrdemServico ordemServico) {

    var documentOutputType = getByDocType(ordemServico.name());

    var ctx = new Context();
    ctx.setVariable("assunto", documentOutputType.getTitulo());
    ctx.setVariable("conteudo", htmlBody);
    ctx.setVariable("dataEmissao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()));
    ctx.setVariable("nomePresidente", getResponsavel(documentOutputType.getResponsavel()));

    return ctx;
  }

  private ParamDocOutputEntity getByDocType(String type) {
    return paramDocOutputEntityRepository.findByTipoDocumentoAndEstado(type, Estado.A.name()).orElseThrow();
  }

  private String getResponsavel(ResponsavelEntity responsavel) {
    return Optional.ofNullable(responsavel)
        .map(ResponsavelEntity::getFunId)
        .map(FuncionarioEntity::getNome)
        .orElse("NOT DEFINED");
  }

}
