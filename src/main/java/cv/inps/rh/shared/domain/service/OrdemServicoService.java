package cv.inps.rh.shared.domain.service;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.ReportHtmlDTO;
import cv.inps.rh.shared.domain.service.documento.DocumentoRenderizado;
import cv.inps.rh.shared.domain.service.documento.DocumentoSaidaRenderer;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.domain.service.ordemservico.OrdemServicoProvider;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OrdemServicoService {

  private final ParamDocOutputEntityRepository repository;
  private final cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository ordemServicoRepository;
  private final Map<OrdemServico, OrdemServicoProvider> providers;
  private final DocumentoSaidaRenderer renderer;

  public OrdemServicoService(ParamDocOutputEntityRepository repository, List<OrdemServicoProvider> providerList,
                             cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository ordemServicoRepository,
                             DocumentoSaidaRenderer renderer) {

    this.repository = repository;
    this.ordemServicoRepository = ordemServicoRepository;
    this.renderer = renderer;

    this.providers = providerList.stream()
        .collect(Collectors.toMap(
            OrdemServicoProvider::getTipo,
            Function.identity()
        ));
  }

  /** Contexto do template os-general a partir do documento já renderizado (título com marcadores incluído). */
  public Context generate(DocumentoRenderizado doc) {
    var ctx = new Context();
    ctx.setVariable("assunto", doc.getTitulo());
    ctx.setVariable("numeroOrdem", ordemServicoRepository.proximoNumero());
    ctx.setVariable("conteudo", doc.getCorpoHtml());
    ctx.setVariable("dataEmissao", formatNow());
    ctx.setVariable("nomePresidente", doc.getResponsavel() != null ? doc.getResponsavel() : "NOT DEFINED");
    return ctx;
  }

  /** Ordem de serviço do tipo para o colaborador: valores do provider do tipo, marcadores pelo motor comum. */
  public DocumentoRenderizado renderizar(OrdemServico tipo, String funcionarioId) {
    var provider = Objects.requireNonNull(providers.get(tipo), "No provider found for ordem de serviço: " + tipo);
    return renderer.render(tipo.name(), provider.buildVariables(funcionarioId));
  }

  public Context generate(OrdemServico tipo, String htmlBody) {

    var documentOutputType = getByDocType(tipo.name());

    var ctx = new Context();

    ctx.setVariable("assunto", documentOutputType.getTitulo());
    // Nº provisório (o próximo do ano): a ordem só é gravada ao validar; o template mostrava "______".
    ctx.setVariable("numeroOrdem", ordemServicoRepository.proximoNumero());
    ctx.setVariable("conteudo", htmlBody);
    ctx.setVariable("dataEmissao", formatNow());
    ctx.setVariable("nomePresidente", getResponsavel(documentOutputType.getResponsavel()));

    return ctx;
  }

  public ReportHtmlDTO content(OrdemServico tipo, String funcionarioId) {
    return new ReportHtmlDTO(renderizar(tipo, funcionarioId).getCorpoHtml());
  }

  private ParamDocOutputEntity getByDocType(String type) {
    return repository.findByTipoDocumentoAndEstado(type, Estado.A.name())
        .orElseThrow(() ->
            new IllegalArgumentException("Documento não encontrado para tipo: " + type)
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
