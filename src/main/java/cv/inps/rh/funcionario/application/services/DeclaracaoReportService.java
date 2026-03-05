package cv.inps.rh.funcionario.application.services;

public interface DeclaracaoReportService {
    String gerarDeclaracao(String declaracaoId, boolean isPreview);
}
