package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.ReportHtmlDTO;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.domain.service.ordemservico.OrdemServicoProvider;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrdemServicoService {

  private final ParamDocOutputEntityRepository repository;

  private final Map<OrdemServico, OrdemServicoProvider> providers;

  public OrdemServicoService(ParamDocOutputEntityRepository repository, List<OrdemServicoProvider> providerList) {

    this.repository = repository;

    this.providers = providerList.stream()
        .collect(Collectors.toMap(
            OrdemServicoProvider::getTipo,
            Function.identity()
        ));
  }

  public Context generate(OrdemServico tipo, String htmlBody) {

    var documentOutputType = getByDocType(tipo.name());

    var ctx = new Context();

    ctx.setVariable("assunto", documentOutputType.getTitulo());
    ctx.setVariable("conteudo", htmlBody);
    ctx.setVariable("dataEmissao", formatNow());
    ctx.setVariable(
        "nomePresidente",
        getResponsavel(documentOutputType.getResponsavel())
    );

    return ctx;
  }

  public ReportHtmlDTO content(OrdemServico tipo, String funcionarioId) {

    var provider = providers.get(tipo);

    if (provider == null) {
      throw new IllegalArgumentException(
          "No provider found for ordem de serviço: " + tipo
      );
    }

    var values = provider.buildVariables(funcionarioId);

    return generateContent(tipo, values);
  }

  private ReportHtmlDTO generateContent(OrdemServico tipo, Map<String, Object> values) {

    var documentOutputType = getByDocType(tipo.name());

    return new ReportHtmlDTO(
        StringSubstitutor.replace(
            documentOutputType.getCorpo(),
            values
        )
    );
  }

  private ParamDocOutputEntity getByDocType(String type) {

    return repository
        .findByTipoDocumentoAndEstado(
            type,
            Estado.A.name()
        )
        .orElseThrow(() ->
            new IllegalArgumentException(
                "Documento não encontrado para tipo: " + type
            )
        );
  }

  private String getResponsavel(ResponsavelEntity responsavel) {

    return Optional.ofNullable(responsavel)
        .map(ResponsavelEntity::getFunId)
        .map(FuncionarioEntity::getNome)
        .orElse("NOT DEFINED");
  }

  private String formatNow() {
    return DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now());
  }
}
