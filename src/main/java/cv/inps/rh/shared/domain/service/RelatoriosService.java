package cv.inps.rh.shared.domain.service;

import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.shared.application.dto.MinioFileDataDTO;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RelatoriosService {

  private final PdfGenerator pdf;
  private final StorageService storageService;

  public RelatoriosService(PdfGenerator pdf, StorageService storageService) {
    this.pdf = pdf;
    this.storageService = storageService;
  }

  @SneakyThrows
  public MinioFileDataDTO ordemServico() {

    Map<String, Object> data = Map.ofEntries(
        Map.entry("numeroOrdem", "15"),
        Map.entry("ano", "2025"),
        Map.entry("assunto", "Pedido de Licença sem Vencimento"),
        Map.entry("periodoMeses", "2"),
        Map.entry("periodoExtenso", "dois"),
        Map.entry("cargo", "Coordenadora"),
        Map.entry("nome", "Maria Fernandes Silva"),
        Map.entry("categoria", "14 E"),
        Map.entry("dataEfeito", "20 de maio de 2025"),
        Map.entry("dataEmissao", "30 de abril de 2025"),
        Map.entry("nomePresidente", "Mário Rui Lopes Fernandes")
    );

    var bytes = pdf.generate("ordem-servico", data);

    storageService.uploadPublicFile(bytes, "ordem-servico.pdf", MediaType.APPLICATION_PDF_VALUE);

    return new MinioFileDataDTO("ordem-servico.pdf", "ordem-servico.pdf");
  }

  public Map<String, Object> recibosSalario() {

    Map<String, Object> recibo = Map.of(
        "modelo", "SA0003",
        "entidade", "Processar Remunerações Gabinete Sistemas de Informação",
        "dataProcessamento", "10/2025",
        "dataEmissao", "27-10-2025 10:54:16",
        "funcionario", Map.of(
            "numero", "10458041",
            "nome", "Nivaldo Cardoso Tavares",
            "vinculo", "Contratado (Contratado - Contratado)"
        ),
        "totais", Map.of(
            "remuneracoes", "112.167",
            "descontos", "22.364",
            "liquido", "89.803"
        ),
        "remuneracoes", List.of(
            Map.of("descricao", "Vencimento Pessoal Contratado (IUR SS)", "valor", "112.167")
        ),
        "descontos", List.of(
            Map.of("descricao", "Retenção Previdência Social (8,5%)", "valor", "9.534"),
            Map.of("descricao", "Retenção IUR (Pessoal INPS)", "valor", "12.830")
        )
    );

    return Map.of("recibos", List.of(recibo, recibo));
  }

  public Map<String, Object> processamentoSalarios() {

    Map<String, Object> funcionario = Map.of(
        "dados", "10116231 - Anilton Pina Brandão - (Contratado - Diretor de Gabinete/15)",
        "lancamentos", List.of(
            Map.of("descricao", "Vencimento Pessoal Contratado (IUR SS)", "valor", "200.319"),
            Map.of("descricao", "Retenção Previdência Social (8,5%)", "valor", "17.707"),
            Map.of("descricao", "Retenção IUR (Pessoal INPS)", "valor", "35.355")
        ),
        "totais", Map.of(
            "remuneracoes", "214.319",
            "descontos", "53.062",
            "liquido", "161.257"
        )
    );

    return Map.of(
        "modelo", "SA0001",
        "dataElaboracao", "27-10-2025 10:50:11",
        "dataProcessamento", "27/10/2025",
        "entidade", "Processar Remunerações Gabinete Sistemas de Informação",
        "funcionarios", List.of(
            funcionario, funcionario, funcionario, funcionario, funcionario, funcionario, funcionario, funcionario,
            funcionario, funcionario, funcionario, funcionario, funcionario, funcionario, funcionario, funcionario
        )
    );
  }
}
