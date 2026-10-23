package cv.inps.rh.funcionario.infrastructure.services;

import cv.inps.rh.funcionario.application.service.DeclaracaoReportService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeclaracaoReportServiceImpl implements DeclaracaoReportService {

    private final DeclaracaoEntityRepository declaracaoRepository;
    private final ParamDocOutputEntityRepository paramDocOutputRepository;
    private final ResourceLoader resourceLoader;
    private final PdfGenerator pdfGenerator;

    // Valor do domínio ORDEM_SERVICO_DECLARACAO (referência DECLARACAO) → ficheiro em templates/declaracao/.
    // Antes procurava "declaracao/<valor>" e nenhum dos 4 tipos tinha ficheiro com esse nome. Os templates
    // partilham o layout (o texto vem do Documento de Saída), por isso um tipo sem ficheiro próprio usa o padrão.
    private static final String TEMPLATE_PADRAO = "declaracao_credito_bancario";
    private static final Map<String, String> TEMPLATES = Map.of(
        "PEDIDO_CREDITO_BANCARIO", "declaracao_credito_bancario",
        "CREDITO_HABITACAO", "declaracao_credito_habitacao",
        "EFEITO_VISTOS", "declaracao_vistos");

    @Transactional(readOnly = true)
    @Override
    public byte[] gerarDeclaracao(String declaracaoId) {

        var id = UUID.fromString(declaracaoId);
        DeclaracaoEntity declaracao = declaracaoRepository.findByUuid(id).orElseThrow(
            () -> IgrpResponseStatusException.notFound("Declaracao not found for id: " + declaracaoId)
        );

        var paramDocOutput = paramDocOutputRepository
            .findByTipoDocumentoAndEstado(declaracao.getTipoDeclaracao(), "A").orElseThrow(
                () -> IgrpResponseStatusException.notFound("Documento Output not found para tipo declaracao: " + declaracao.getTipoDeclaracao())
            );

      var templateName = "declaracao/" + TEMPLATES.getOrDefault(declaracao.getTipoDeclaracao(), TEMPLATE_PADRAO);

      var resource = resourceLoader.getResource(
          "classpath:/templates/" + templateName + ".html");

      if (!resource.exists()) {
        throw IgrpResponseStatusException.badRequest(
            "Template não implementado para tipo: " + declaracao.getTipoDeclaracao());
      }

      boolean watermark = !"SIM".equalsIgnoreCase(declaracao.getDecisaoRh());

      Map<String, Object> data = new HashMap<>();
      data.put("declaracao", declaracao);
      data.put("param", paramDocOutput);
      data.put("watermark", watermark);

      return pdfGenerator.generate(templateName, data);
    }
}
