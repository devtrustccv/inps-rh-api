package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.configuracao.application.services.model.ResponsavelSectionData;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface ResponsavelEntityRepository extends
    JpaRepository<ResponsavelEntity, Long>,
    JpaSpecificationExecutor<ResponsavelEntity> {

  default ResponsavelEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ResponsavelEntity not found for id: " + id));
  }

  List<ResponsavelEntity> findAllByInstitId_idAndSecaoId_uuid(Long institutoId, UUID seccaoUuid);

  // Notificação: só responsáveis ACTIVOS. O soft-delete de RH_T_RESPONSAVEL põe estado='I' (ver
  // ResponsavelService), pelo que sem este filtro notificar-se-ia uma chefia já removida da
  // direcção. Estado é String nesta entidade — usar Estado.A.name().

  // 1º nível: a chefia da secção onde o colaborador está colocado. Por uuid da secção e SEM
  // instit_id — a secção já determina a direção, e há colocações em que o instit_id da mobilidade
  // não bate certo com o inst_id da secção.
  List<ResponsavelEntity> findAllBySecaoId_uuidAndEstado(UUID seccaoUuid, String estado);

  // 2º nível: fallback para a chefia DA DIREÇÃO — a linha com secao_id NULL (ver saveResponsaveis:
  // "There can only be one direction responsible"). Não devolve os responsáveis das secções irmãs.
  // Devolve List e não Optional como o findByInstitId_IdAndSecaoIdIsNull: o soft-delete deixa
  // linhas antigas em 'I' na mesma direção, e um Optional rebentaria com NonUniqueResultException.
  List<ResponsavelEntity> findAllByInstitId_idAndSecaoIdIsNullAndEstado(Long institutoId, String estado);

  Optional<ResponsavelEntity> findByFunId_Uuid(UUID funcionarioUuid);

  @Query("""
          SELECT NEW cv.inps.rh.configuracao.application.services.model.ResponsavelSectionData(
                s.uuid,
                s.nome,
                r.id,
                r.funId.uuid,
                r.funId.nome
          )
          FROM SecaoEntity s
          LEFT JOIN ResponsavelEntity r
              ON r.secaoId.id = s.id
              AND r.institId.id = :instId
              AND r.estado = 'A'
          LEFT JOIN FuncionarioEntity f
                ON r.funId.id = f.id
          WHERE s.instId.id = :instId AND s.estado = 'A'
      """)
  List<ResponsavelSectionData> findAllSectionsByDirection(Long instId);

  Optional<ResponsavelEntity> findByInstitId_IdAndSecaoIdIsNull(
      Long direcao
  );

  List<ResponsavelEntity> findByInstitIdAndSecaoIdIsNotNullAndEstado(
      DirecaoEntity direcao,
      String estado
  );
}
