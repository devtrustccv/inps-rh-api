package cv.inps.rh.funcionario.infrastructure.services;

import cv.inps.rh.funcionario.application.service.DeclaracaoReportService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MockDeclaracaoReportService implements DeclaracaoReportService {

    private final DeclaracaoEntityRepository declaracaoRepository;

    @Transactional(readOnly = true)
    @Override
    public String gerarDeclaracao(String declaracaoId) {

        var id = UUID.fromString(declaracaoId);
        DeclaracaoEntity declaracao = declaracaoRepository.findByUuid(id).orElseThrow(
            () -> IgrpResponseStatusException.notFound("Declaracao not found for id: " + declaracaoId)
        );

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("<h1>Declaração de Vencimento</h1>");
        reportContent.append("<p>Declaramos que o(a) Sr(a). ")
                     .append(declaracao.getPedidoId().getFunId().getNome())
                     .append(" aufere o vencimento mensal de X.</p>");

        // Aplica a marca d'água se for preview ou se a declaração não estiver validada
        boolean aplicarMarcaAgua = !"SIM".equalsIgnoreCase(declaracao.getDecisaoRh());

        if (aplicarMarcaAgua) {
            reportContent.append("<h2 style='color:red;'>NÃO VÁLIDO</h2>");
        }

        return reportContent.toString();
    }
}
