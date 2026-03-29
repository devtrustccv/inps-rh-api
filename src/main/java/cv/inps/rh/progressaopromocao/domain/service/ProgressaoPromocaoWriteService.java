package cv.inps.rh.progressaopromocao.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.igrp.platform.filemanager.StorageService;
import cv.inps.rh.progressaopromocao.application.dto.AnexarOrdemServicoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValEvolucaoCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PdfGenerator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.sql.DataSource;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Transactional
@AllArgsConstructor
@Service
public class ProgressaoPromocaoWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressaoPromocaoWriteService.class);

  private final EvolucaoCarreiraEntityRepository evolucaoCarreiraEntityRepository;
  private final ValEvolucaoCarreiraEntityRepository valEvolucaoCarreiraEntityRepository;
  private final SimEvolucaoCarreiraEntityRepository simEvolucaoCarreiraEntityRepository;
  private final OrdemServicoEntityRepository ordemServicoEntityRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final PdfGenerator pdfGenerator;
  private final StorageService storageService;
  private final DataSource dataSource;

  public void anexarOrdemServico(AnexarOrdemServicoRequestDTO request) {

    var ev = evolucaoCarreiraEntityRepository.findByUuidOrThrow(request.getEvolucaoCarreiraUuid());
    var funId = ev.getTiprel().getFunId();

    documentoEntityRepository.findByReferenciaNameAndReferenciaUuidAndReferenciaIdAndFunIdAndEstado(
        TableName.RH_T_EVOLUCAO_CARREIRA.name(),
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
          document.setReferenciaName(TableName.RH_T_EVOLUCAO_CARREIRA.name());
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
    var id = ordemServicoEntityRepository.save(os).getId().toString();

    ev.setOrdemServicoId(id);
    evolucaoCarreiraEntityRepository.save(ev);
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

    var ev = evolucaoCarreiraEntityRepository.findById(id).orElseThrow();

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withProcedureName("CONFIRMAR_PROGRESSAO_PROMO")
        .declareParameters(
            new SqlParameter("P_val_evol_carr_ID", Types.NUMERIC),
            new SqlParameter("P_ORDEM_SERVICO_ID", Types.NUMERIC)
        );

    call.execute(Map.of(
        "P_val_evol_carr_ID", id,
        "P_ORDEM_SERVICO_ID", ev.getOrdemServicoId()
    ));
  }

  public void sendToHistory(List<Long> ids) {

    var numberOfRows = valEvolucaoCarreiraEntityRepository.marcarComoHistorico(ids);

    LOGGER.debug("Number of rows sent to History: {}", numberOfRows);
  }

  @SneakyThrows
  public byte[] extrairOrdemServico(List<Long> ids) {

    if (ids == null || ids.isEmpty())
      throw IgrpResponseStatusException.badRequest("Dados insuficientes para extração da ordem de serviço");

    var formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale.of("pt", "PT"));

    var uniqueFilename = UuidCreator.getTimeOrderedEpoch().toString();

    var collaborators = valEvolucaoCarreiraEntityRepository.getInformacaoColaboradores(ids);

    var now = LocalDate.now();

    var context = new Context();
    context.setVariable("nomePresidente", "Mário Rui Lopes Fernandes");
    context.setVariable("numeroOrdem", "123456789"); //todo fix this
    context.setVariable("ano", String.valueOf(now.getYear()));
    context.setVariable("dataEmissao", formatter.format(now));
    context.setVariable("colaboradores", collaborators);
    var bytes = pdfGenerator.generate("os-progressao-cargo", context); //todo validate witch template

    storageService.uploadFile(bytes, uniqueFilename, MediaType.APPLICATION_PDF.toString());

    var numberOfRows = valEvolucaoCarreiraEntityRepository.setFileIdToRows(ids, uniqueFilename);
    LOGGER.debug("Number of rows updated with file Id {}: {}", numberOfRows, uniqueFilename);

    return bytes;
  }
}
