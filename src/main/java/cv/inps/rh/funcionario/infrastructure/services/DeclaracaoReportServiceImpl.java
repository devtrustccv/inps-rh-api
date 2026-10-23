package cv.inps.rh.funcionario.infrastructure.services;

import cv.inps.rh.funcionario.application.service.DeclaracaoReportService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.documento.DocumentoSaidaRenderer;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVRelacaoLaboralEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeclaracaoReportServiceImpl implements DeclaracaoReportService {

    private final DeclaracaoEntityRepository declaracaoRepository;
    private final DocumentoSaidaRenderer documentoSaidaRenderer;
    private final RhVRelacaoLaboralEntityRepository relacaoLaboralRepository;
    private final DomainEntityRepository domainEntityRepository;
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

        // Texto do Documento de Saída do tipo, com os marcadores ${...} substituídos (antes saía cru).
        var paramDocOutput = documentoSaidaRenderer.render(declaracao.getTipoDeclaracao(), valoresBase(declaracao));

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

    /**
     * Valores que qualquer declaração pode usar no texto (${nomeColaborador}, ${cargo}…). Para um tipo com dados
     * próprios, acrescentar aqui (ou montar outro mapa) — ver docs/guia_documentos_saida_marcadores.html.
     */
    private Map<String, Object> valoresBase(DeclaracaoEntity declaracao) {
        var v = new LinkedHashMap<String, Object>();
        var funcionario = declaracao.getPedidoId() != null ? declaracao.getPedidoId().getFunId() : declaracao.getFunId();
        if (funcionario != null) {
            v.put("nomeColaborador", funcionario.getNome());
            v.put("nif", funcionario.getNif());
        }
        if (declaracao.getTiprelId() != null)
            relacaoLaboralRepository.findById(declaracao.getTiprelId().getId()).ifPresent(r -> {
                v.put("cargo", r.getCargoDesc());
                v.put("categoria", r.getEscalaoDesc());
                v.put("carreira", r.getCarreiraDesc());
                v.put("vinculo", r.getVinculoDesc());
                v.put("direcao", r.getDirecaoDesc());
                v.put("seccao", r.getSeccaoDesc());
            });
        v.put("finalidade", declaracao.getFinalidade() == null ? null
            : domainEntityRepository.getActiveDomainByCode("FINALIDADE_DECLARACAO")
                .getOrDefault(declaracao.getFinalidade(), declaracao.getFinalidade()));
        v.put("entidadeDestinatario", declaracao.getEntidadeDestinado());
        v.put("dataPedido", declaracao.getDataPedido() != null
            ? DateFormatter.EXTENDED_DATE_PT.format(declaracao.getDataPedido()) : null);
        v.put("dataEmissao", DateFormatter.EXTENDED_DATE_PT.format(LocalDate.now()));
        return v;
    }
}
