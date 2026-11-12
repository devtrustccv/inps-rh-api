package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.AumentoSalarialRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSimulacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AumentoSalarialWriteService {

  private final AumentoSalarialEntityRepository aumentoSalarialEntityRepository;
  private final AumentoSimulacaoEntityRepository aumentoSimulacaoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final DataSource dataSource;
  private final ApplicationAuditorAware applicationAuditorAware;

  public void saveAumentoSalarial(AumentoSalarialRequestDTO request) {

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_AUMENTO_SALARIAL_DB")
        .withProcedureName("SIMULAR_AUMENTO")
        .declareParameters(
            new SqlParameter("P_DESCRICAO", Types.VARCHAR),
            new SqlParameter("P_MOTIVO", Types.VARCHAR),
            new SqlParameter("P_DATA_REFERENTE", Types.DATE),
            new SqlParameter("PERCENTAGEM", Types.NUMERIC),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(Map.of(
        "P_DESCRICAO", request.getDesignacao(),
        "P_MOTIVO", request.getMotivo(),
        "P_DATA_REFERENTE", java.sql.Date.valueOf(request.getDataReferente()),
        "PERCENTAGEM", request.getPercentagem(),
        "P_USER_ID", "0", // TODO 17/05/2026 11:14 current user
        "P_USER_NAME", applicationAuditorAware.getCurrentSubjectName()
    ));
  }

  public void updateAumentoSalarial(String salaryIncreaseId, AumentoSalarialRequestDTO request) {

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId).orElseThrow();
    salaryIncrease.setDescricao(request.getDesignacao());
    salaryIncrease.setMotivo(request.getMotivo());
    salaryIncrease.setDataReferente(request.getDataReferente());
    salaryIncrease.setPercentagem(request.getPercentagem());
    aumentoSalarialEntityRepository.save(salaryIncrease);
  }

  /**
   * Valida (A) ou rejeita (I) via RH_PK_AUMENTO_SALARIAL_DB.REGISTAR_AUMENTO. A procedure só atualiza a
   * RH_T_VALIDACAO na rejeição; na validação o registo de validação é fechado aqui.
   */
  @Transactional
  public void validar(String salaryIncreaseId, String estadoValidacao) {

    if (!Estado.A.name().equals(estadoValidacao) && !Estado.I.name().equals(estadoValidacao))
      throw IgrpResponseStatusException.badRequest("O estado da validação tem de ser A (validar) ou I (rejeitar).");

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Aumento salarial não encontrado: " + salaryIncreaseId));

    if (!Estado.P.name().equals(salaryIncrease.getEstado()))
      throw IgrpResponseStatusException.badRequest("Só é possível validar ou rejeitar aumentos salariais pendentes.");

    // Sem detalhe (RH_T_AUMENTO_SALARIAL_DET, gerado pelo SIMULAR_AUMENTO) o REGISTAR_AUMENTO com 'A'
    // alteraria os escalões sem aplicar o novo salário a nenhum colaborador.
    if (Estado.A.name().equals(estadoValidacao)
        && !aumentoSimulacaoEntityRepository.existsByAumentoSalId(salaryIncrease.getId()))
      throw IgrpResponseStatusException.badRequest(
          "Este aumento salarial não tem colaboradores simulados; não pode ser validado.");

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_AUMENTO_SALARIAL_DB")
        .withProcedureName("REGISTAR_AUMENTO")
        .declareParameters(
            new SqlParameter("P_ESTADO_VALIDACAO", Types.VARCHAR),
            new SqlParameter("P_ID_AUMENTO", Types.VARCHAR),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(Map.of(
        "P_ESTADO_VALIDACAO", estadoValidacao,
        "P_ID_AUMENTO", salaryIncrease.getId(),
        "P_USER_ID", "0", // TODO 17/05/2026 11:14 current user
        "P_USER_NAME", applicationAuditorAware.getCurrentSubjectName()
    ));

    if (Estado.A.name().equals(estadoValidacao)) {
      validacaoEntityRepository.findByReferenciaUuidAndEstadoAndTipoAccaoAndReferenciaName(
              UUID.fromString(salaryIncrease.getUuid()), Estado.P, "INSERT", "AUMENTO_SALARIAL")
          .ifPresent(validacao -> {
            validacao.setEstado(Estado.A);
            validacaoEntityRepository.save(validacao);
          });
    }
  }

}
