package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.dto.ValidacaoDetalheDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Grelha "Detalhe de alterações" da <b>Alteração de Escalão/Cargo</b> (referência {@code
 * ALTERACAO_ESCALAO}) calculada <b>on-the-fly</b>, usada como <b>fallback</b> para movimentos antigos
 * que ainda não têm linhas persistidas em {@code RH_T_VALIDACAO_DETALHE}. Os movimentos novos são
 * gravados na escrita pelo {@link EscalaoDetalheDiffWriter#persistir} e lidos pela via comum
 * ({@code ValidacaoDetalheReadService}); o {@code GetDetalheAlteracoesQueryHandler} escolhe a fonte.
 *
 * <p>O diff em si (antes→depois, só campos alterados) é delegado ao {@link EscalaoDetalheDiffWriter}
 * ({@code javers.compare} de dois snapshots), que é também quem persiste — mantendo uma única fonte de
 * verdade para formatação e rótulos. Aqui só se resolve "antes" (predecessor) e "depois" (tiprel
 * pendente) e se carimba autor/data a partir da auditoria JPA do pendente.
 */
@Service
@RequiredArgsConstructor
public class AlteracaoEscalaoDetalheReadService {

  private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final EscalaoDetalheDiffWriter diffWriter;

  @Transactional(readOnly = true)
  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    var validacao = validacaoEntityRepository.findByUuid(validacaoUuid).orElse(null);
    if (validacao == null || validacao.getTiprelId() == null) {
      return List.of();
    }
    // "depois" = o tiprel pendente desta validação; "antes" = o tiprel que ele clonou (predecessor).
    TiposRelacionamentoEntity depois = validacao.getTiprelId();
    TiposRelacionamentoEntity antes = depois.getTiprelId();

    List<ValidacaoDetalheDTO> linhas = diffWriter.comparar(antes, depois);

    // Autor/data da alteração vêm do próprio registo do tiprel pendente (auditoria JPA).
    String autor = depois.getCreatedBy();
    String quando = dataHora(depois.getCreatedDate());
    linhas.forEach(l -> {
      l.setAlteradoPor(autor);
      l.setDataAlteracao(quando);
    });
    return linhas;
  }

  private String dataHora(LocalDateTime dt) {
    return dt == null ? null : dt.format(DATA_HORA);
  }
}
