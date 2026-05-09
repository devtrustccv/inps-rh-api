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
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
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

  public Context fimComissaoServico(String htmlBody) {

    var documentOutputType = getByDocType(OrdemServico.FIM_COMISSAO_SERVICO.name());

    var ctx = new Context();
    ctx.setVariable("assunto", documentOutputType.getTitulo());
    ctx.setVariable("conteudo", htmlBody);
    ctx.setVariable("dataEmissao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()));
    ctx.setVariable("nomePresidente", getResponsavel(documentOutputType.getResponsavel()));

    return ctx;
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

    var documentOutputType = getByDocType(OrdemServico.CONVERSAO_CONTRATO.name());

    var ctx = new Context();
    ctx.setVariable("assunto", documentOutputType.getTitulo());
    ctx.setVariable("conteudo", htmlBody);
    ctx.setVariable("dataEmissao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()));
    ctx.setVariable("nomePresidente", getResponsavel(documentOutputType.getResponsavel()));

    return ctx;
  }

  public ReportHtmlDTO conversaoContratoContent(String funcionarioId) {

    var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var documentOutputType = getByDocType(OrdemServico.CONVERSAO_CONTRATO.name());
    var values = Map.of(
        "nomeColaborador", fun.getNome()
    );
    return new ReportHtmlDTO(StringSubstitutor.replace(documentOutputType.getCorpo(), values));
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
