package cv.inps.rh.shared.domain.service;

import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.processamento.domain.service.processamentosalarial.report.model.ProcessamentoSalarialReport;
import cv.inps.rh.shared.application.dto.MinioFileDataDTO;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RelatoriosService {

  private static final String AREM = "AREM";
  private static final String SEPARATOR = " - ";
  private final PdfGenerator pdf;
  private final StorageService storageService;
  private final JdbcTemplate jdbcTemplate;

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

  public Context processamentoSalarios(Long processamentoId, String tipo) {

    var context = new Context();

    var data = getProcessamentoSalarialReportData(processamentoId, tipo);
    if (data.isEmpty())
      return context;

    var funcionarios = new ArrayList<Funcionarios>(data.size());

    var dataFirst = data.getFirst();
    var dataProcessamento = dataFirst.dataProcessamento();
    var centroCusto = dataFirst.centroDeCusto();

    var grouped = data.stream()
        .collect(Collectors.groupingBy(
            ProcessamentoSalarialReport::cargo, // TODO 22/01/2026 21:24 group by cargo id here to be more performant
            Collectors.groupingBy(ProcessamentoSalarialReport::funId)
        ));

    grouped.forEach((_, funMap) ->
        funMap.forEach((_, rows) -> {

          var lancamentos = rows.stream()
              .sorted(Comparator.comparing(r -> !AREM.equals(r.tipo())))
              .map(r -> new Lancamentos(r.descricao(), r.valor()))
              .toList();

          var firstRow = rows.getFirst();

          var func = new Funcionarios(
              firstRow.descricaoMovimento(),
              firstRow.nif() + SEPARATOR + firstRow.nomeCargoEscalao(),
              lancamentos,
              firstRow.totalRemuneracoes(),
              firstRow.totalDescontos(),
              firstRow.totalLiquido()
          );

          funcionarios.add(func);
        })
    );

    context.setVariable("dataElaboracao", LocalDateTime.now().format(DateFormatter.DATE_TIME));
    context.setVariable("dataProcessamento", dataProcessamento.format(DateFormatter.DATE));
    context.setVariable("entidade", "Processar Remunerações " + centroCusto);
    context.setVariable("funcionarios", funcionarios);
    return context;
  }

  private List<ProcessamentoSalarialReport> getProcessamentoSalarialReportData(Long processamentoId, String tipo) {
    var sql = """
            SELECT
                tm.DESCRICAO AS DESCRICAO_MOVIMENTO,
                p.TIPO,
                p.DATA_PROCESSAMENTO,
                p.CENTRO_DE_CUSTO,
                p.CARGO,
                p.FUN_ID,
                p.DESCRICAO,
                p.VALOR,
                p.NIF,
                p.NOME_CARGO_ESCALAO,
                p.TOTAL_REMUNERACOES,
                p.TOTAL_DESCONTOS,
                p.TOTAL_LIQUIDO
            FROM INPSRH.PROC_SAL_CC p JOIN RH_TIPO_MOVIMENTOS tm ON tm.ID = p.TM_ID
        """;

    return jdbcTemplate.query(sql, (rs, _) ->
        new ProcessamentoSalarialReport(
            rs.getString("TIPO"),
            rs.getString("DESCRICAO_MOVIMENTO"),
            rs.getDate("DATA_PROCESSAMENTO").toLocalDate(),
            rs.getString("CENTRO_DE_CUSTO"),
            rs.getString("CARGO"),
            rs.getLong("FUN_ID"),
            rs.getString("DESCRICAO"),
            rs.getLong("VALOR"),
            rs.getLong("NIF"),
            rs.getString("NOME_CARGO_ESCALAO"),
            rs.getLong("TOTAL_REMUNERACOES"),
            rs.getLong("TOTAL_DESCONTOS"),
            rs.getLong("TOTAL_LIQUIDO")
        )
    );
  }

  public record Lancamentos(String descricao, Long valor) {
  }

  public record Funcionarios(
      String shortDesc,
      String dados,
      List<Lancamentos> lancamentos,
      Long remuneracoes,
      Long descontos,
      Long liquido
  ) {
  }

}
