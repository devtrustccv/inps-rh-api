package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessamentoFuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessamentoFuncionarioRepository extends
    JpaRepository<ProcessamentoFuncionarioEntity, Long>,
    JpaSpecificationExecutor<ProcessamentoFuncionarioEntity> {

  default ProcessamentoFuncionarioEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "ProcessamentoFuncionarioEntity not found for id: " + id));
  }

  /**
   * Reproduz a coluna PROCESSAMENTO da vista RH_V_CARREIRA: indica se a carreira
   * ja foi processada em folha, i.e. existe algum tiprel dessa carreira com
   * registo em RH_T_PROC_FUNCIONARIOS.
   */
  boolean existsByTiprel_CarreiraId_Id(Long carreiraId);

  /**
   * Indica se um tipo de relacionamento já foi processado em folha — mesma lógica da vista
   * (RH_V_CONTRATO/RH_V_RELACAO_LABORAL): EXISTS em RH_T_PROC_FUNCIONARIOS por TIPREL_ID.
   */
  boolean existsByTiprel_Id(Long tiprelId);

  /**
   * Reproduz a coluna PROCESSAMENTO da vista RH_V_MOBILIDADE: existe um tiprel desta mobilidade
   * (RH_T_TIPOS_RELACIONAMENTO.MOB_ID) com registo em RH_T_PROC_FUNCIONARIOS. Usado para bloquear a
   * edição de mobilidades já processadas em folha.
   */
  boolean existsByTiprel_MobId_Id(Long mobId);

  /**
   * Já existe processamento em folha do tiprel cujo período de referência começa dentro do intervalo
   * (mês). Usado na substituição (caso de uso, Caso 4.1/4.2): se o 1º mês já foi processado, a
   * diferença desse mês junta-se ao mês seguinte em vez de gerar um DEF_REMUNERACOES próprio.
   */
  boolean existsByTiprel_IdAndDataReferenciaDeBetween(
      Long tiprelId, java.time.LocalDate de, java.time.LocalDate ate);
}
