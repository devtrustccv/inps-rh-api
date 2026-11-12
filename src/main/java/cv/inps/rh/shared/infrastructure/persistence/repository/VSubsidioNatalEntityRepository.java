package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.VSubsidioNatalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * A view RH_V_SUBSIDIO_NATAL só mostra ESTADO A/I e exige vínculo activo; a Lista Subsídio de Natal
 * passou a ler RH_T_SUBSIDIOS (SubsidioEntityRepository.findSubsidioNatal), onde o ESTADO é o de
 * validação (P/A) e o activo/inactivo é FLG_ATIVO_INACTIVO, como no Subsídio de Férias.
 */
@Repository
public interface VSubsidioNatalEntityRepository extends JpaRepository<VSubsidioNatalEntity, Long>, JpaSpecificationExecutor<VSubsidioNatalEntity> {
}
