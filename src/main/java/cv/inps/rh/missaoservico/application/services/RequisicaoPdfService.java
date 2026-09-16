package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoColabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosInstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoRequisicaoColabEntityRepository;
import cv.inps.rh.shared.util.PdfGenerator;
import cv.inps.rh.shared.util.ValorPorExtenso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.ESTADO_ATIVO;

/**
 * Nota de encomenda (requisição) de um processo de missão — ecrã "Extrair Requisição" da spec,
 * segundo o modelo 3.2.4.2.2.
 *
 * <p>O modelo de dados só guarda o valor total da requisição (RH_T_MISSAO_REQUISICAO.VALOR_TOTAL),
 * por isso a descrição é uma linha única — o serviço a favor de todos os colaboradores — com esse
 * valor, como no modelo.
 */
@Service
@RequiredArgsConstructor
public class RequisicaoPdfService {

  private static final Locale PT = Locale.of("pt", "PT");
  private static final DateTimeFormatter DATA_EXTENSO = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PT);

  private final PdfGenerator pdfGenerator;
  private final DadosInstituicaoEntityRepository dadosInstituicaoRepository;
  private final MissaoRequisicaoColabEntityRepository missaoRequisicaoColabRepository;

  public byte[] gerar(MissaoRequisicaoEntity requisicao) {
    return pdfGenerator.generate("missao-requisicao", modelo(requisicao));
  }

  /** Nº apresentado na nota de encomenda, como no modelo da spec — ex.: "RMS-2026-0134". */
  public static String notaEncomenda(MissaoRequisicaoEntity requisicao) {
    return "RMS-%d-%04d".formatted(requisicao.getAno(), requisicao.getNrRequisacao());
  }

  public static String nomeFicheiro(MissaoRequisicaoEntity requisicao) {
    return "requisicao_" + notaEncomenda(requisicao) + ".pdf";
  }

  private Map<String, Object> modelo(MissaoRequisicaoEntity r) {
    var prestador = r.getMissaoPrestId();
    var param = prestador.getParamPrestId();
    var processo = prestador.getMissaoProcessoId();
    var missao = prestador.getMissaoServId();
    var tipoDesc = processo != null ? TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso()).getDescricao() : "";

    var instituicao = dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(ESTADO_ATIVO).orElse(null);

    var colaboradores = missaoRequisicaoColabRepository.findAllByMissaoRequisicaoId_IdIn(List.of(r.getId())).stream()
        .filter(rc -> ESTADO_ATIVO.equals(rc.getEstado()))
        .sorted(Comparator.comparing(MissaoRequisicaoColabEntity::getId))
        .map(rc -> rc.getMissaoColabId().getFunId() != null ? rc.getMissaoColabId().getFunId().getNome() : "")
        .toList();

    var model = new HashMap<String, Object>();
    model.put("logoPath", Optional.ofNullable(getClass().getResource("/static/img/inps_logo.png"))
        .map(java.net.URL::toExternalForm).orElse(""));
    model.put("nomeInstituicao", instituicao != null ? instituicao.getNome() : "Instituto Nacional de Previdência Social");
    model.put("nifInstituicao", instituicao != null && instituicao.getNif() != null ? String.valueOf(instituicao.getNif()) : "");
    model.put("notaEncomenda", notaEncomenda(r));
    var local = instituicao != null && StringUtils.hasText(instituicao.getLocalidade()) ? capitalizar(instituicao.getLocalidade()) : "Praia";
    model.put("localData", local + ", aos " + LocalDate.now().format(DATA_EXTENSO));
    model.put("nrRequisicao", String.valueOf(r.getNrRequisacao()));
    model.put("prestadorNome", param != null ? param.getNome() : prestador.getNome());
    model.put("prestadorNif", param != null && param.getNif() != null ? param.getNif() : "—");
    model.put("prestadorMorada", param != null && param.getMorada() != null ? param.getMorada() : "—");
    model.put("descricao", tipoDesc + " a favor de " + juntar(colaboradores));
    model.put("valor", formatarValor(r.getValorTotal()));
    model.put("total", r.getValorTotal() != null ? formatarValor(r.getValorTotal()) + " ECV" : "—");
    model.put("totalExtenso", r.getValorTotal() != null ? capitalizar(ValorPorExtenso.escudos(r.getValorTotal())) : "—");
    model.put("elaboradoPor", missao.getAutorizadoPor());
    // Spec: "Aprovado por — quem submeteu a etapa" (RH_T_MISSAO_REQUISICAO.USER_REGISTO_NAME)
    model.put("aprovadoPor", r.getCreatedBy());
    return model;
  }

  /** Valor como no modelo: "1 900"; as casas decimais só aparecem quando existem. */
  private String formatarValor(BigDecimal valor) {
    if (valor == null)
      return "";
    var nf = NumberFormat.getNumberInstance(PT);
    nf.setMinimumFractionDigits(valor.stripTrailingZeros().scale() > 0 ? 2 : 0);
    nf.setMaximumFractionDigits(2);
    return nf.format(valor);
  }

  /** "A", "A e B", "A, B e C". */
  private static String juntar(List<String> nomes) {
    if (nomes.size() <= 1)
      return nomes.isEmpty() ? "" : nomes.getFirst();
    return String.join(", ", nomes.subList(0, nomes.size() - 1)) + " e " + nomes.getLast();
  }

  private String capitalizar(String s) {
    var t = s.trim().toLowerCase(PT);
    return t.isEmpty() ? t : Character.toUpperCase(t.charAt(0)) + t.substring(1);
  }
}
