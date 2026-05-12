package cv.inps.rh.shared.domain.service;

import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.processamento.domain.service.processamentosalarial.report.model.ProcessamentoSalarialReport;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcPagEntity;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcRemunEntity;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM/yyyy");
  private final PdfGenerator pdf;
  private final StorageService storageService;
  private final JdbcTemplate jdbcTemplate;
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;

  public Map<String, Object> recibosSalario(Long procFuncionarioId) {

    var remunRows = procSalCcRemunEntityRepository.findByProcFuncId(procFuncionarioId);
    var pagRows = procSalCcPagEntityRepository.findByProcFuncId(procFuncionarioId);

    if (remunRows.isEmpty() && pagRows.isEmpty()) {
      return Map.of("recibos", List.of());
    }

    ProcSalCcRemunEntity header = remunRows.isEmpty() ? null : remunRows.getFirst();

    String entidade = header != null ? ("Processar Remunerações " + header.getCentroDeCusto()) : "";
    String dataProcessamento = header != null ? header.getDataProcessamento().format(MONTH_YEAR) : "";
    String dataEmissao = LocalDateTime.now().format(DateFormatter.DATE_TIME);
    String nome = header != null ? header.getNome() : "";
    String vinculo = header != null ? header.getRelacao() : "";
    String nif = header != null ? String.valueOf(header.getNif()) : "";
    Long totRemun = header != null ? header.getTotalRemuneracoes() : 0L;
    Long totDes = header != null ? header.getTotalDescontos() : 0L;
    Long totLiq = header != null ? header.getTotalLiquido() : 0L;

    var remuneracoes = remunRows.stream()
        .map(r -> Map.of("descricao", (Object) r.getDescricao(), "valor", (Object) r.getValor()))
        .toList();

    var descontos = pagRows.stream()
        .map(r -> Map.of("descricao", (Object) r.getDescricao(), "valor", (Object) r.getValor()))
        .toList();

    var recibo = Map.ofEntries(
        Map.entry("entidade", entidade),
        Map.entry("dataProcessamento", dataProcessamento),
        Map.entry("dataEmissao", dataEmissao),
        Map.entry("funcionario", Map.of("numero", nif, "nome", nome, "vinculo", vinculo)),
        Map.entry("totais", Map.of("remuneracoes", totRemun, "descontos", totDes, "liquido", totLiq)),
        Map.entry("remuneracoes", remuneracoes),
        Map.entry("descontos", descontos)
    );

    return Map.of("recibos", List.of(recibo));
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
