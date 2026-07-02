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
import org.springframework.util.StringUtils;
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

  public Map<String, Object> recibosSalario(Long procFuncionarioId, String funId) {

    var id = StringUtils.hasText(funId) ?
        funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId)).getId() : null;

    var remunRows = id != null ?
        procSalCcRemunEntityRepository.findByProcSalIdAndFunId(procFuncionarioId, id) :
        procSalCcRemunEntityRepository.findByProcSalId(procFuncionarioId);

    var pagRows = id != null ?
        procSalCcPagEntityRepository.findByProcSalIdAndFunId(procFuncionarioId, id) :
        procSalCcPagEntityRepository.findByProcSalId(procFuncionarioId);

    if (remunRows.isEmpty() && pagRows.isEmpty())
      return Map.of("recibos", List.of());

    ProcSalCcRemunEntity header = remunRows.isEmpty() ? null : remunRows.getFirst();

    var hasHeader = header != null;
    var entidade = hasHeader ? ("Processar Remunerações " + header.getCentroDeCusto()) : "";
    var dataProcessamento = hasHeader ? header.getDataProcessamento().format(MONTH_YEAR) : "";
    var dataEmissao = LocalDateTime.now().format(DateFormatter.DATE_TIME);

    var nome = hasHeader ? header.getNome() : "";
    var vinculo = hasHeader ? header.getRelacao() : "";
    var nif = hasHeader && header.getNif() != null ? String.valueOf(header.getNif()) : "";
    var totRemun = hasHeader ? header.getTotalRemuneracoes() : 0L;
    var totDes = hasHeader ? header.getTotalDescontos() : 0L;
    var totLiq = hasHeader ? header.getTotalLiquido() : 0L;

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
        Map.entry("remuneracoes", remuneracoes),
        Map.entry("descontos", descontos),
        Map.entry(
            "funcionario", Map.of(
                "numero", nif,
                "nome", nome,
                "vinculo", vinculo
            )
        ),
        Map.entry(
            "totais", Map.of(
                "remuneracoes", totRemun,
                "descontos", totDes,
                "liquido", totLiq
            )
        )
    );

    return Map.of("recibos", List.of(recibo));
  }

  public Context processamentoSalarios(Long processamentoId, String tipo, String funId) {

    var id = StringUtils.hasText(funId) ?
        funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId)).getId() : null;

    var data = procSalCcEntityEntityRepository.findAllByFilters(
        processamentoId,
        StringUtils.hasText(tipo) ? tipo : null,
        id
    );
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
