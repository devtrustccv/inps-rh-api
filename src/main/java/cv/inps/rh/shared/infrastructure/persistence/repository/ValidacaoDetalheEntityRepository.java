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
   * Ordem de APRESENTAÇÃO da grelha: a ordem de declaração dos campos no módulo ({@code ORDEM}), e não
   * a ordem em que as linhas foram inseridas. Linhas antigas, anteriores à coluna, têm {@code ORDEM}
   * nula e caem para o fim (NULLS LAST do Oracle em ASC) pela ordem de id — comportamento anterior.
   */
  List<ValidacaoDetalheEntity> findByValidacaoId_UuidOrderByTabelaNameAscOrdemAscIdAsc(UUID validacaoUuid);

  boolean existsByValidacaoId_Uuid(UUID validacaoUuid);

  /** Suporta a flag que diz ao frontend se deve mostrar o botão "Detalhe de alterações". */
  List<ValidacaoDetalheEntity> findByValidacaoId_UuidIn(List<UUID> validacaoUuids);
}
