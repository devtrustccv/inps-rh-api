package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoDetalheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ValidacaoDetalheEntityRepository extends JpaRepository<ValidacaoDetalheEntity, Long> {

  /**
   * Regra da spec (linha 1612): "trazer informações de detalhe na tabela RH_T_VALIDACAO_DETALHE,
   * onde VALIDACAO_ID = ID de RH_T_VALIDACAO".
   */
  List<ValidacaoDetalheEntity> findByValidacaoId_UuidOrderByTabelaNameAscIdAsc(UUID validacaoUuid);

  /**
   * Ordem de APRESENTAÇÃO da grelha: por tabela, depois por LINHA ({@code TABELA_ID}) e só então pela
   * ordem de declaração dos campos ({@code ORDEM}).
   *
   * <p>Agrupar por linha importa nas coleções: com duas contas bancárias alteradas, a grelha mostrava
   * "Nº de conta" duas vezes intercalado com os campos da outra conta, sem se perceber a qual pertencia
   * cada uma. Linhas antigas (sem {@code TABELA_ID}/{@code ORDEM}) caem para o fim pelo id, como antes.
   */
  List<ValidacaoDetalheEntity> findByValidacaoId_UuidOrderByTabelaNameAscTabelaIdAscOrdemAscIdAsc(UUID validacaoUuid);

  boolean existsByValidacaoId_Uuid(UUID validacaoUuid);

  /** Suporta a flag que diz ao frontend se deve mostrar o botão "Detalhe de alterações". */
  List<ValidacaoDetalheEntity> findByValidacaoId_UuidIn(List<UUID> validacaoUuids);
}
