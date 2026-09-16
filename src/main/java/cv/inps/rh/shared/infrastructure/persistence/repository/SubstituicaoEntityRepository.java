package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface SubstituicaoEntityRepository extends
    JpaRepository<SubstituicaoEntity, Long>,
    JpaSpecificationExecutor<SubstituicaoEntity>
{

      default SubstituicaoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"SubstituicaoEntity not found for id: " + id));
      }

    Optional<SubstituicaoEntity> findByUuid(UUID idSusbtituicao);

  Page<SubstituicaoEntity> findBySubstituidoTiprelId_FunId_Uuid(UUID funUUid, Pageable pageable);

  Page<SubstituicaoEntity> findBySubstituidoTiprelId_FunId_Uuid_AndEstadoIn(
      UUID substituidoTiprelIdFunIdUuid,
      List<Estado> estados,
      Pageable pageable
  );

  /**
   * Substituições onde o funcionário é o SUBSTITUTO (quem substitui) — o dossiê mostra as
   * substituições que o próprio registou. Inclui A/P/I (mostra também pendentes).
   */
  Page<SubstituicaoEntity> findBySubstitutoTiprelId_FunId_Uuid_AndEstadoIn(
      UUID substitutoTiprelIdFunIdUuid,
      List<Estado> estados,
      Pageable pageable
  );

  /**
   * Substituições do SUBSTITUÍDO (o colaborador que está a ser substituído) que se sobrepõem no
   * tempo ao período [novoInicio, novoFim]. Usado como guard: não se pode registar nova substituição
   * de um colaborador que já tenha outra a cobrir o mesmo período. Sobreposição de intervalos:
   * dataInicio_existente <= novoFim AND dataFim_existente >= novoInicio. Filtra pelos estados
   * indicados (tipicamente A/P — as já activas e as pendentes de validação).
   */
  List<SubstituicaoEntity>
  findBySubstituidoTiprelId_FunId_UuidAndEstadoInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
      UUID substituidoTiprelIdFunIdUuid,
      List<Estado> estados,
      java.time.LocalDate novoFim,
      java.time.LocalDate novoInicio
  );

  /**
   * Substituições do SUBSTITUTO (o colaborador que substitui) que se sobrepõem no tempo ao período
   * [novoInicio, novoFim]. Guard: o mesmo substituto não pode estar a cobrir dois colaboradores em
   * períodos sobrepostos. Mesma regra de sobreposição do método do substituído.
   */
  List<SubstituicaoEntity>
  findBySubstitutoTiprelId_FunId_UuidAndEstadoInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
      UUID substitutoTiprelIdFunIdUuid,
      List<Estado> estados,
      java.time.LocalDate novoFim,
      java.time.LocalDate novoInicio
  );

}
