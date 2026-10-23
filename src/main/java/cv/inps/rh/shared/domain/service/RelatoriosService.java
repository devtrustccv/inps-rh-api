package cv.inps.rh.shared.domain.service;

import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcPagEntity;
import cv.inps.rh.processamento.infrastructure.persistence.entity.ProcSalCcRemunEntity;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcPagEntityRepository;
import cv.inps.rh.processamento.infrastructure.repositories.ProcSalCcRemunEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcSalCcEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcSalCcEntityEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.InpsStringUtils;
import cv.inps.rh.shared.util.NumberUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RelatoriosService {

  private static final String AREM = "AREM";
  private static final String SEPARATOR = " - ";
  private static final String SALARIO = "SAL";
  // fica no fim do TreeMap (ordenação por texto) e o template não mostra o título
  private static final String SEM_VENCIMENTO = "￿";
  private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MM/yyyy");

  private final ProcSalCcRemunEntityRepository procSalCcRemunEntityRepository;
  private final ProcSalCcPagEntityRepository procSalCcPagEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ProcSalCcEntityEntityRepository procSalCcEntityEntityRepository;

  /**
   * A linha "Valor Líquido" dos pagamentos é o total a receber, não um desconto.
   */
  private static boolean isValorLiquido(String descricao) {
    var normalizada = InpsStringUtils.normalizeText(descricao);
    return normalizada != null && normalizada.toLowerCase(Locale.ROOT).startsWith("valor liquido");
  }

  private static String formatar(DecimalFormat format, Long valor) {
    return format.format(valor != null ? valor : 0L);
  }

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

    // um recibo por colaborador (sem colaborador indicado saíam todos misturados num só)
    var remunPorFuncionario = remunRows.stream()
        .collect(Collectors.groupingBy(ProcSalCcRemunEntity::getFunId, LinkedHashMap::new, Collectors.toList()));
    var pagPorFuncionario = pagRows.stream()
        .collect(Collectors.groupingBy(ProcSalCcPagEntity::getFunId, LinkedHashMap::new, Collectors.toList()));

    var funIds = new LinkedHashSet<Long>();
    funIds.addAll(remunPorFuncionario.keySet());
    funIds.addAll(pagPorFuncionario.keySet());

    var nifs = nifsFuncionarios(funIds);
    var dataEmissao = LocalDateTime.now().format(DateFormatter.DATE_TIME);

    var recibos = funIds.stream()
        .map(fun -> recibo(
            remunPorFuncionario.getOrDefault(fun, List.of()),
            pagPorFuncionario.getOrDefault(fun, List.of()),
            nifs.get(fun),
            dataEmissao))
        .sorted(Comparator.comparing(r -> String.valueOf(((Map<?, ?>) r.get("funcionario")).get("nome"))))
        .toList();

    return Map.of("recibos", recibos);
  }

  private Map<String, Object> recibo(List<ProcSalCcRemunEntity> remunRows, List<ProcSalCcPagEntity> pagRows,
                                     Long nifFuncionario, String dataEmissao) {

    var remunHeader = remunRows.isEmpty() ? null : remunRows.getFirst();
    var pagHeader = pagRows.isEmpty() ? null : pagRows.getFirst();

    var centroCusto = remunHeader != null ? remunHeader.getCentroDeCusto() : pagHeader.getCentroDeCusto();
    var dataProc = remunHeader != null ? remunHeader.getDataProcessamento() : pagHeader.getDataProcessamento();
    var nome = remunHeader != null ? remunHeader.getNome() : pagHeader.getNome();
    var vinculo = remunHeader != null ? remunHeader.getRelacao() : pagHeader.getRelacao();
    var totRemun = remunHeader != null ? remunHeader.getTotalRemuneracoes() : pagHeader.getTotalRemuneracoes();
    var totDes = remunHeader != null ? remunHeader.getTotalDescontos() : pagHeader.getTotalDescontos();
    var totLiq = remunHeader != null ? remunHeader.getTotalLiquido() : pagHeader.getTotalLiquido();

    var usDecimalFormat = NumberUtils.dotDecimalFormat();

    var remuneracoes = remunRows.stream()
        .map(r -> Map.of(
            "descricao", r.getDescricao(),
            "valor", (Object) usDecimalFormat.format(r.getValor())
        ))
        .toList();

    var descontos = pagRows.stream()
        .filter(r -> !isValorLiquido(r.getDescricao()))
        .map(r -> Map.of(
            "descricao", r.getDescricao(),
            "valor", (Object) usDecimalFormat.format(r.getValor())
        ))
        .toList();

    return Map.ofEntries(
        Map.entry("entidade", "Processar Remunerações " + centroCusto),
        Map.entry("dataProcessamento", dataProc != null ? dataProc.format(MONTH_YEAR) : ""),
        Map.entry("dataEmissao", dataEmissao),
        Map.entry("remuneracoes", remuneracoes),
        Map.entry("descontos", descontos),
        Map.entry(
            "funcionario", Map.of(
                "numero", nifFuncionario != null ? String.valueOf(nifFuncionario) : "",
                "nome", nome != null ? nome : "",
                "vinculo", vinculo != null ? vinculo : ""
            )
        ),
        Map.entry(
            "totais", Map.of(
                "remuneracoes", formatar(usDecimalFormat, totRemun),
                "descontos", formatar(usDecimalFormat, totDes),
                "liquido", formatar(usDecimalFormat, totLiq)
            )
        )
    );
  }

  /**
   * NIF de RH_T_FUNCIONARIOS — o da vista de processamento não é fiável.
   */
  private Map<Long, Long> nifsFuncionarios(Collection<Long> funIds) {
    var nifs = new HashMap<Long, Long>();
    funcionarioEntityRepository.findAllById(funIds.stream().filter(Objects::nonNull).toList())
        .forEach(f -> {
          if (f.getNif() != null) nifs.put(f.getId(), f.getNif());
        });
    return nifs;
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

    var dataFirst = data.getFirst();
    var dataProcessamento = dataFirst.getDataProcessamento();
    var centroCusto = dataFirst.getCentroDeCusto();

    var porFuncionario = data.stream()
        .collect(Collectors.groupingBy(ProcSalCcEntity::getFunId, LinkedHashMap::new, Collectors.toList()));

    var nifs = nifsFuncionarios(porFuncionario.keySet());
    var usDecimalFormat = NumberUtils.dotDecimalFormat();

    // spec: agrupar pela DESCRICAO da linha de vencimento (SHORT_DESC = 'SAL'), ex. "Vencimento Pessoal contratado"
    var porVencimento = new TreeMap<String, List<Funcionarios>>();

    porFuncionario.forEach((fun, rows) -> {

      var lancamentos = rows.stream()
          .filter(r -> !isValorLiquido(r.getDescricao()))
          .sorted(Comparator.comparing(r -> !AREM.equals(r.getTipo())))
          .map(r -> new Lancamentos(r.getDescricao(), usDecimalFormat.format(r.getValor())))
          .toList();

      var firstRow = rows.getFirst();
      var nif = nifs.get(fun);

      var vencimento = rows.stream()
          .filter(r -> SALARIO.equals(r.getShortDesc()))
          .map(ProcSalCcEntity::getDescricao)
          .filter(Objects::nonNull)
          .findFirst()
          .orElse(SEM_VENCIMENTO);

      var func = new Funcionarios(
          firstRow.getNome(),
          nif != null ? nif + SEPARATOR + firstRow.getNomeCargoEscalao() : firstRow.getNomeCargoEscalao(),
          lancamentos,
          formatar(usDecimalFormat, firstRow.getTotalRemuneracoes()),
          formatar(usDecimalFormat, firstRow.getTotalDescontos()),
          formatar(usDecimalFormat, firstRow.getTotalLiquido())
      );

      porVencimento.computeIfAbsent(vencimento, _ -> new ArrayList<>()).add(func);
    });

    var grupos = porVencimento.entrySet().stream()
        .map(e -> new GrupoVencimento(
            SEM_VENCIMENTO.equals(e.getKey()) ? null : e.getKey(),
            e.getValue().stream()
                .sorted(Comparator.comparing(f -> f.nome() == null ? "" : f.nome()))
                .toList()))
        .toList();

    context.setVariable("dataElaboracao", LocalDateTime.now().format(DateFormatter.DATE_TIME));
    context.setVariable("dataProcessamento", dataProcessamento.format(DateFormatter.DATE));
    context.setVariable("entidade", "Processar Remunerações " + centroCusto);
    context.setVariable("grupos", grupos);
    return context;
  }

  public record GrupoVencimento(
      String vencimento,
      List<Funcionarios> funcionarios
  ) {
  }

  public record Funcionarios(
      String nome,
      String dados,
      List<Lancamentos> lancamentos,
      String remuneracoes,
      String descontos,
      String liquido
  ) {
  }

  public record Lancamentos(String descricao, String valor) {
  }

}
