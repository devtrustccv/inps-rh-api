package cv.inps.rh.transversal.application.commands;

import cv.igrp.framework.core.domain.CommandHandler;
import cv.igrp.framework.stereotype.IgrpCommandHandler;
import cv.inps.rh.transversal.application.service.DossierTransformerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cv.inps.rh.transversal.application.dto.DossierResponseDTO;
import cv.inps.rh.transversal.application.service.DossierQueryBuilderService;
import cv.inps.rh.transversal.application.service.ValidacaoDossierService;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ObterDossierColaboradorCommandHandler implements CommandHandler<ObterDossierColaboradorCommand, ResponseEntity<DossierResponseDTO>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObterDossierColaboradorCommandHandler.class);

    private final ValidacaoDossierService validacaoDossierService;
    private final DossierQueryBuilderService dossierQueryBuilderService;
    private final DossierTransformerService resultadoDossierTransformerService;


    @IgrpCommandHandler
    public ResponseEntity<DossierResponseDTO> handle(ObterDossierColaboradorCommand command) {

        LOGGER.debug("ObterDossierColaboradorCommand : {}", command);

        // 1. Validar a requisição
        validacaoDossierService.validar(command);

        // 2. Executar a query
        List<Tuple> resultados = dossierQueryBuilderService.executarQueryAgrupada(command.getDossierrequest());

        LOGGER.info("Query executada. {} linhas de resultado obtidas.", resultados.size());

        // 3. Transformar o resultado plano em hierárquico
        DossierResponseDTO response = resultadoDossierTransformerService.transformar(
                resultados, command.getDossierrequest().getAgrupadores());

        response.setFiltros(command.getDossierrequest().getFiltros());

        return ResponseEntity.ok(response);
    }

}
