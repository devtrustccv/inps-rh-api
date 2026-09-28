package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoEntityRepository extends
                JpaRepository<AvaliacaoEntity, Long>,
                JpaSpecificationExecutor<AvaliacaoEntity> {

        @Query("""
                            SELECT a
                            FROM AvaliacaoEntity a
                            WHERE a.funcionario.id = :funId
                            ORDER BY a.ano DESC
                        """)
        List<AvaliacaoEntity> findUltimasAvaliacoes(
                        @Param("funId") Long funId,
                        Pageable pageable);

        /**
         * Há alguma avaliação lançada para este ano? Usado como guard da inativação da
         * parametrização de componentes: uma vez definido um objectivo no ano, o ciclo
         * já não pode ser desligado.
         */
        boolean existsByAno(Integer ano);

        List<AvaliacaoEntity> findAllByFuncionario_IdAndAno(Long funId, Integer ano);

        /**
         * A avaliação de objectivos comuns de um ano e abrangência. Não tem colaborador, e a
         * direção distingue as de abrangência DIRECAO entre si. Usado para tornar a definição
         * idempotente: reenviar o mesmo formulário acrescenta períodos em vez de duplicar.
         */
        @Query("""
                            SELECT a FROM AvaliacaoEntity a
                             WHERE a.ano = :ano
                               AND UPPER(a.abrangencia) = UPPER(:abrangencia)
                               AND a.funcionario IS NULL
                               AND ((:institId IS NULL AND a.institId IS NULL)
                                    OR a.institId.id = :institId)
                        """)
        List<AvaliacaoEntity> findComuns(@Param("ano") Integer ano,
                        @Param("abrangencia") String abrangencia,
                        @Param("institId") Long institId);

        /**
         * Os anos com objectivos comuns (INPS ou DIRECAO, sem colaborador), do mais recente
         * para o mais antigo. É a linha pai da lista "Objectivos / Avaliação Comuns".
         */
        @Query(value = """
                            SELECT DISTINCT a.ano FROM AvaliacaoEntity a
                             WHERE a.funcionario IS NULL
                               AND UPPER(a.abrangencia) IN ('INPS', 'DIRECAO')
                               AND (a.estado IS NULL OR a.estado <> 'E')
                               AND (:ano IS NULL OR a.ano = :ano)
                             ORDER BY a.ano DESC
                        """,
                        countQuery = """
                            SELECT COUNT(DISTINCT a.ano) FROM AvaliacaoEntity a
                             WHERE a.funcionario IS NULL
                               AND UPPER(a.abrangencia) IN ('INPS', 'DIRECAO')
                               AND (a.estado IS NULL OR a.estado <> 'E')
                               AND (:ano IS NULL OR a.ano = :ano)
                        """)
        org.springframework.data.domain.Page<Integer> findAnosComuns(@Param("ano") Integer ano, Pageable pageable);

        /** Os objectivos comuns de um ano: o do INPS e um por direção. */
        @Query("""
                            SELECT a FROM AvaliacaoEntity a
                              LEFT JOIN FETCH a.institId
                             WHERE a.ano = :ano
                               AND a.funcionario IS NULL
                               AND UPPER(a.abrangencia) IN ('INPS', 'DIRECAO')
                               AND (a.estado IS NULL OR a.estado <> 'E')
                             ORDER BY a.id
                        """)
        List<AvaliacaoEntity> findComunsDoAno(@Param("ano") Integer ano);

        Optional<AvaliacaoEntity> findByUuid(UUID uuid);

        default AvaliacaoEntity findByUuidOrThrow(UUID uuid) {
                return findByUuid(uuid)
                                .orElseThrow(() -> cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException
                                                .notFound("AvaliacaoEntity not found for id: " + uuid));
        }
}
