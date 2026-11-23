package cv.inps.rh.progressaopromocao.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValEvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SimEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValEvolucaoCarreiraEntityRepository;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ProgressaoPromocaoWriteService {

  private static final String PACKAGE_PROMO_PROGRESSAO = "RH_PK_PROMO_PROGRESSAO_DB";
  private static final String ORIGEM_MANUAL = "MANUAL";

  private final ValEvolucaoCarreiraEntityRepository valEvolucaoCarreiraEntityRepository;
  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final OrdemServicoEntityRepository ordemServicoEntityRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final PdfGenerator pdfGenerator;
  private final StorageService storageService;
  private final DataSource dataSource;
  private final ApplicationAuditorAware applicationAuditorAware;

  public void simular() {

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName(PACKAGE_PROMO_PROGRESSAO)
        .withProcedureName("PR_SIMULAR")
        .declareParameters(
            new SqlParameter("P_DATA_REFERENCIA", Types.DATE),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR),
            new SqlParameter("P_ORIGEM", Types.VARCHAR)
        );

    call.execute(new MapSqlParameterSource()
        .addValue("P_DATA_REFERENCIA", Date.valueOf(LocalDate.now(ZoneId.systemDefault())))
        .addValue("P_USER_ID", 0L)
        .addValue("P_USER_NAME", applicationAuditorAware.getCurrentSubjectName())
        .addValue("P_ORIGEM", ORIGEM_MANUAL));
  }

  public void anexarOrdemServico(AnexarOrdemServicoRequestDTO request) {

    var ev = valEvolucaoCarreiraEntityRepository.findByUuidOrThrow(request.getEvolucaoCarreiraUuid());
    var funId = ev.getTiprel().getFunId();

    documentoEntityRepository.findByReferenciaNameAndReferenciaUuidAndReferenciaIdAndFunIdAndEstado(
        TableName.RH_T_VAL_EVOLUCAO_CARREIRA.name(),
        UUID.fromString(ev.getUuid()),
        ev.getId().toString(),
        funId,
        Estado.A
    ).ifPresentOrElse(obj -> {
          obj.setUrl(request.getOrdemServicoUrl());
          documentoEntityRepository.save(obj);
        },
        () -> {
          var document = new DocumentoEntity();
          document.setUuid(UuidCreator.getTimeOrderedEpoch());
          document.setReferenciaName(TableName.RH_T_VAL_EVOLUCAO_CARREIRA.name());
          document.setReferenciaId(ev.getId().toString());
          document.setReferenciaUuid(UUID.fromString(ev.getUuid()));
          document.setEstado(Estado.A);
          document.setFunId(funId);
          document.setUrl(request.getOrdemServicoUrl());
          documentoEntityRepository.save(document);
        });

    var os = new OrdemServicoEntity();
    os.setNuOrdem(request.getNumero());
    os.setDescricao(request.getDescricao());
    os.setReferente(request.getRequerente());
    os.setEstado(Estado.A);
    os.setUuid(UuidCreator.getTimeOrderedEpoch());
    os.setFunId(funId);
    ev.setOrdemServicoId(ordemServicoEntityRepository.save(os).getId());
    valEvolucaoCarreiraEntityRepository.save(ev);
  }

  public void editarSimulacao(Long id, String elegivel, EstadoValidacao validacao, String observacao) {

    var row = simEvolucaoCarreiraEntityRepository.findByIdOrThrow(id);
    row.setFlgElegivel(elegivel);
    row.setObsMotivoAlteracao(observacao);
    simEvolucaoCarreiraEntityRepository.save(row);

    if (validacao == EstadoValidacao.SIM) {
      var obj = new ValEvolucaoCarreiraEntity();
      obj.setCarreiraIdDe(row.getCarreiraIdDe());
      obj.setEscalaoIdDe(row.getEscalaoIdDe());
      obj.setEscalaoIdPara(row.getEscalaoIdPara());
      obj.setTiprel(row.getTiprel());
      obj.setDataReferente(row.getDataReferente());
      obj.setObservacao(row.getObservacao());
      obj.setTipo(row.getTipo());
      obj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      obj.setEstado(Estado.A.name());
      obj.setObsMotivoAlteracao(observacao);
      valEvolucaoCarreiraEntityRepository.save(obj);
    }
  }

  public void validar(List<Long> ids) {

    var rowsToBeValidated = simEvolucaoCarreiraEntityRepository.findAllById(ids);

    for (var row : rowsToBeValidated) {
      var obj = new ValEvolucaoCarreiraEntity();
      obj.setCarreiraIdDe(row.getCarreiraIdDe());
      obj.setEscalaoIdDe(row.getEscalaoIdDe());
      obj.setEscalaoIdPara(row.getEscalaoIdPara());
      obj.setDataReferente(row.getDataReferente());
      obj.setTiprel(row.getTiprel());
      obj.setObservacao(row.getObservacao());
      obj.setTipo(row.getTipo());
      obj.setEstado(Estado.A.name());
      obj.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      valEvolucaoCarreiraEntityRepository.save(obj);
    }

    simEvolucaoCarreiraEntityRepository.deleteAll(rowsToBeValidated);
  }

  public void confirmar(Long id) {

    if (!valEvolucaoCarreiraEntityRepository.existsById(id))
      throw IgrpResponseStatusException.notFound("Validação de progressão/promoção não encontrada: " + id);

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName(PACKAGE_PROMO_PROGRESSAO)
        .withProcedureName("CONFIRMAR_PROGRESSAO_PROMO")
        .declareParameters(
            new SqlParameter("P_VAL_EVOL_CARR_ID", Types.NUMERIC),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(new MapSqlParameterSource()
        .addValue("P_VAL_EVOL_CARR_ID", id)
        .addValue("P_USER_ID", 0L)
        .addValue("P_USER_NAME", applicationAuditorAware.getCurrentSubjectName()));
  }

  public void sendToHistory(List<Long> ids) {

    var numberOfRows = valEvolucaoCarreiraEntityRepository.marcarComoHistorico(ids);

    log.debug("Number of rows sent to History: {}", numberOfRows);
  }

  @SneakyThrows
  public byte[] extrairOrdemServico(List<Long> ids) {

    if (ids == null || ids.isEmpty())
      throw IgrpResponseStatusException.badRequest("Dados insuficientes para extração da ordem de serviço");

    var formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.of("pt", "PT"));

    var uniqueFilename = UuidCreator.getTimeOrderedEpoch().toString();

    var collaborators = valEvolucaoCarreiraEntityRepository.getInformacaoColaboradores(ids);

    var now = LocalDate.now(ZoneId.systemDefault());

    var context = new Context();
    context.setVariable("nomePresidente", "Mário Rui Lopes Fernandes");
    context.setVariable("numeroOrdem", UuidCreator.getTimeOrderedEpoch().toString());
    context.setVariable("ano", String.valueOf(now.getYear()));
    context.setVariable("dataEmissao", formatter.format(now));
    context.setVariable("colaboradores", collaborators);
    var bytes = pdfGenerator.generate("os-progressao-cargo", context); //todo validate witch template

    storageService.uploadFile(bytes, uniqueFilename, MediaType.APPLICATION_PDF.toString());

    var numberOfRows = valEvolucaoCarreiraEntityRepository.setFileIdToRows(ids, uniqueFilename);

    log.debug("Number of rows updated with file Id {}: {}", numberOfRows, uniqueFilename);

    return bytes;
  }
}
