package cv.inps.rh.shared.domain.service;

import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcRemunEntity;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcSalCcEntityEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RelatoriosService {

  private static final String AREM = "AREM";
  private static final String SEPARATOR = " - ";
  private static final String SEM_CARGO = "__SEM_CARGO__";
  private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM/yyyy");
  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ProcSalCcEntityEntityRepository procSalCcEntityEntityRepository;

  public Map<String, Object> recibosSalario(Long procFuncionarioId) {

    var remunRows = procSalCcRemunEntityRepository.findByProcFuncId(procFuncionarioId);
    var pagRows = procSalCcPagEntityRepository.findByProcFuncId(procFuncionarioId);

    if (remunRows.isEmpty() && pagRows.isEmpty())
      return Map.of("recibos", List.of());

    ProcSalCcRemunEntity header = remunRows.isEmpty() ? null : remunRows.getFirst();

    var entidade = header != null ? ("Processar Remunerações " + header.getCentroDeCusto()) : "";
    var dataProcessamento = header != null ? header.getDataProcessamento().format(MONTH_YEAR) : "";
    var dataEmissao = LocalDateTime.now().format(DateFormatter.DATE_TIME);
    String nome = header != null ? header.getNome() : "";
    String vinculo = header != null ? header.getRelacao() : "";
    String nif = header != null ? String.valueOf(header.getNif()) : "";
    Long totRemun = header != null ? header.getTotalRemuneracoes() : 0L;
    Long totDes = header != null ? header.getTotalDescontos() : 0L;
    Long totLiq = header != null ? header.getTotalLiquido() : 0L;

    var remuneracoes = remunRows.stream()
        .map(r -> Map.of("descricao", r.getDescricao(), "valor", (Object) r.getValor()))
        .toList();

    var descontos = pagRows.stream()
        .map(r -> Map.of("descricao", r.getDescricao(), "valor", (Object) r.getValor()))
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

  public Context processamentoSalarios(Long processamentoId, String tipo, String funId) {

    var id = funId != null && !funId.isBlank() ?
        funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId)).getId() : null;

    var data = procSalCcEntityEntityRepository.findAllByFilters(processamentoId, tipo, id);
    if (data.isEmpty())
      return new Context();

    var context = new Context();

    var funcionarios = new ArrayList<Funcionarios>(data.size());

    var dataFirst = data.getFirst();
    var dataProcessamento = dataFirst.getDataProcessamento();
    var centroCusto = dataFirst.getCentroDeCusto();

    var grouped = data.stream()
        .collect(Collectors.groupingBy(
            p -> p.getCargo() == null ? SEM_CARGO : p.getCargo(),
            Collectors.groupingBy(ProcSalCcEntity::getFunId)
        ));

    grouped.forEach((_, funMap) ->
        funMap.forEach((_, rows) -> {

          var lancamentos = rows.stream()
              .sorted(Comparator.comparing(r -> !AREM.equals(r.getTipo())))
              .map(r -> new Lancamentos(r.getDescricao(), r.getValor()))
              .toList();

          var firstRow = rows.getFirst();

          var func = new Funcionarios(
              firstRow.getDescricao(),
              firstRow.getNif() != null ? firstRow.getNif() + SEPARATOR + firstRow.getNomeCargoEscalao() : firstRow.getNomeCargoEscalao(),
              lancamentos,
              firstRow.getTotalRemuneracoes(),
              firstRow.getTotalDescontos(),
              firstRow.getTotalLiquido()
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
