package cv.inps.rh.shared.domain.service;

import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.shared.application.dto.MinioFileDataDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcSalCcEntityRepository;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RelatoriosService {

  private final PdfGenerator pdf;
  private final StorageService storageService;
  private final ProcSalCcEntityRepository procSalCcEntityRepository;

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

    var funcionarios = new ArrayList<Map<String, Object>>();

    var data = procSalCcEntityRepository.findAll(); // todo findByProcSalIdAndTipoAndShortDesc
    if (data.isEmpty())
      return Map.of();

    var dataProcessamento = data.getFirst().getDataProcessamento();
    var centroCusto = data.getFirst().getCentroDeCusto();

    data.stream()
        //.filter(obj -> "SAL".equals(obj.getShortDesc()))
        .collect(Collectors.groupingBy(ProcSalCcEntity::getFunId))
        .forEach((_, v) -> {

          var first = v.getFirst();
          var shortDesc = first.getShortDesc();
          var nif = first.getNif();
          var cargo = first.getNomeCargoEscalao();
          var totalRemuneracoes = first.getTotalRemuneracoes();
          var totalDescontos = first.getTotalDescontos();
          var totalLiquido = first.getTotalLiquido();

          var lancamentos = new ArrayList<Map<String, Object>>();
          v.forEach(obj -> lancamentos.add(Map.of("descricao", obj.getDescricao(), "valor", obj.getValor())));

          Map<String, Object> row = Map.of(
              "shortDesc", shortDesc,
              "dados", nif + " - " + cargo,
              "lancamentos", lancamentos,
              "totais", Map.of(
                  "remuneracoes", totalRemuneracoes,
                  "descontos", totalDescontos,
                  "liquido", totalLiquido
              )
          );
          funcionarios.add(row);
        });

    var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    return Map.of(
        "dataElaboracao", LocalDateTime.now().format(formatter),
        "dataProcessamento", dataProcessamento,
        "entidade", "Processar Remunerações de " + centroCusto,
        "funcionarios", funcionarios
    );
  }
}
