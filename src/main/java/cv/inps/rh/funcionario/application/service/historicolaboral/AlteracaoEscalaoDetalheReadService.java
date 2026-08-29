package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.funcionario.application.service.GestaoLaboralValidacaoDetalheDescriptor;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamCargoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Grelha "Detalhe de alterações" da <b>Alteração de Escalão/Cargo</b> (referência {@code
 * ALTERACAO_ESCALAO}). Serve o mesmo {@link ValidacaoDetalheDTO} que o caminho JaVers, mas por uma via
 * <b>dedicada e read-only</b> — NÃO passa pelo {@code JaversValidacaoDetalheReadService} nem toca em
 * nenhum outro fluxo.
 *
 * <p><b>Porquê fora do JaVers:</b> o {@link TiposRelacionamentoEntity} é registado como <em>Shallow
 * Reference</em> (por causa da auto-referência {@code tiprelId} — a cadeia de tiprels anteriores — e das
 * muitas FKs pesadas, que sem shallow faziam cada commit percorrer o grafo em 20-50 s). Sendo shallow, o
 * JaVers grava o tiprel com estado vazio ({@code STATE={}}), pelo que os seus próprios campos
 * (escalão/cargo/salário/datas) nunca aparecem no histórico. Aqui não é preciso: o "antes" e o "depois"
 * já existem como <b>duas linhas reais</b> — o tiprel pendente ({@code validacao.tiprelId}) e o seu
 * predecessor ({@code pendente.tiprelId}) — e comparam-se diretamente.
 *
 * <p>Os rótulos e a allow-list de campos reutilizam o {@link GestaoLaboralValidacaoDetalheDescriptor},
 * mantendo uma só fonte de verdade.
 */
@Service
@RequiredArgsConstructor
public class AlteracaoEscalaoDetalheReadService {

  private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
  private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
  private static final String TABELA = "RH_T_TIPOS_RELACIONAMENTO";

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final GestaoLaboralValidacaoDetalheDescriptor descriptor;

  @Transactional(readOnly = true)
  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    var validacao = validacaoEntityRepository.findByUuid(validacaoUuid).orElse(null);
    if (validacao == null || validacao.getTiprelId() == null) {
      return List.of();
    }
    // "depois" = o tiprel pendente desta validação; "antes" = o tiprel que ele clonou (predecessor).
    TiposRelacionamentoEntity depois = validacao.getTiprelId();
    TiposRelacionamentoEntity antes = depois.getTiprelId();

    Map<String, String> rotulos = descriptor.rotulos();
    List<ValidacaoDetalheDTO> linhas = new ArrayList<>();

    linha(linhas, rotulos, "escalaoId", escalao(antes), escalao(depois));
    linha(linhas, rotulos, "cargoId", cargo(antes), cargo(depois));
    linha(linhas, rotulos, "salario", valor(antes == null ? null : antes.getSalario()),
        valor(depois.getSalario()));
    linha(linhas, rotulos, "moeda", antes == null ? null : antes.getMoeda(), depois.getMoeda());
    linha(linhas, rotulos, "tipoSituacao", antes == null ? null : antes.getTipoSituacao(),
        depois.getTipoSituacao());
    linha(linhas, rotulos, "dataInicio", data(antes == null ? null : antes.getDataInicio()),
        data(depois.getDataInicio()));
    linha(linhas, rotulos, "dataFim", data(antes == null ? null : antes.getDataFim()),
        data(depois.getDataFim()));
    linha(linhas, rotulos, "obs", antes == null ? null : antes.getObs(), depois.getObs());

    // Autor/data da alteração vêm do próprio registo do tiprel pendente (auditoria JPA).
    String autor = depois.getCreatedBy();
    String quando = dataHora(depois.getCreatedDate());
    linhas.forEach(l -> {
      l.setAlteradoPor(autor);
      l.setDataAlteracao(quando);
      l.setTabelaName(TABELA);
    });
    return linhas;
  }

  /** Só adiciona a linha quando há diferença real antes→depois (semântica de EDIÇÃO). */
  private void linha(List<ValidacaoDetalheDTO> acc, Map<String, String> rotulos, String prop,
      String antes, String depois) {
    if (Objects.equals(antes, depois)) {
      return;
    }
    var dto = new ValidacaoDetalheDTO();
    dto.setCampoAlterado(rotulos.getOrDefault(prop, prop));
    dto.setValorAnterior(antes);
    dto.setValorNovo(depois);
    acc.add(dto);
  }

  private String escalao(TiposRelacionamentoEntity tr) {
    ParamEscalaoEntity e = tr == null ? null : tr.getEscalaoId();
    if (e == null) {
      return null;
    }
    if (e.getCodigo() != null && !e.getCodigo().isBlank()) {
      return e.getCodigo();
    }
    // Fallback: nível/escala (mesmo formato da lista de Gestão Laboral, ex.: "13/A").
    String nivel = e.getNivelReferencia() == null ? "" : String.valueOf(e.getNivelReferencia());
    String esc = e.getEscalao() == null ? "" : e.getEscalao();
    String display = (nivel + "/" + esc).trim();
    return "/".equals(display) ? String.valueOf(e.getId()) : display;
  }

  private String cargo(TiposRelacionamentoEntity tr) {
    ParamCargoEntity c = tr == null ? null : tr.getCargoId();
    return c == null ? null : c.getNome();
  }

  private String valor(BigDecimal v) {
    return v == null ? null : v.stripTrailingZeros().toPlainString();
  }

  private String data(LocalDate d) {
    return d == null ? null : d.format(DATA);
  }

  private String dataHora(LocalDateTime dt) {
    return dt == null ? null : dt.format(DATA_HORA);
  }
}
