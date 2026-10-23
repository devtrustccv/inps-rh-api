package cv.inps.rh.configuracao.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.configuracao.application.dto.DocumentoRenderizadoDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.documento.DocumentoSaidaRenderer;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * Pré-visualização sem gravar: sem corpo usa o documento ativo do tipo; com corpo usa o texto enviado.
 * Guia: docs/guia_documentos_saida_marcadores.html.
 */
@Component
@RequiredArgsConstructor
public class PreVisualizarDocumentoCommandHandler implements CommandHandler<PreVisualizarDocumentoCommand, ResponseEntity<DocumentoRenderizadoDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PreVisualizarDocumentoCommandHandler.class);
    private final DocumentoSaidaRenderer renderer;

    @IgrpCommandHandler
    @Transactional(readOnly = true)
    public ResponseEntity<DocumentoRenderizadoDTO> handle(PreVisualizarDocumentoCommand command) {
        LOGGER.debug("PreVisualizarDocumentoCommand: {}", command);
        var req = command.getPrevisualizardocumentorequest();
        if (req == null)
            throw IgrpResponseStatusException.badRequest("Indique o tipoDocumento ou o corpo a pré-visualizar.");

        ParamDocOutputEntity doc;
        if (StringUtils.hasText(req.getCorpo())) {
            doc = new ParamDocOutputEntity();
            doc.setTipoDocumento(req.getTipoDocumento());
            doc.setTitulo(req.getTitulo());
            doc.setCorpo(req.getCorpo());
            doc.setAssinadoPor(req.getAssinadoPor());
        } else if (StringUtils.hasText(req.getTipoDocumento())) {
            doc = renderer.documentoAtivo(req.getTipoDocumento());
        } else {
            throw IgrpResponseStatusException.badRequest("Indique o tipoDocumento ou o corpo a pré-visualizar.");
        }

        var r = renderer.render(doc, req.getValores());
        return ResponseEntity.ok(new DocumentoRenderizadoDTO(r.getTipoDocumento(), r.getTitulo(), r.getCorpo(),
            r.getCorpoHtml(), r.getAssinadoPor(), r.getResponsavel(), new ArrayList<>(r.getMarcadoresEmFalta())));
    }
}
