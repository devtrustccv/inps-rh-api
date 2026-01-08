package cv.inps.rh.processamento.domain.service.processamentosalarial.api;

import cv.inps.rh.processamento.domain.service.processamentosalarial.api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProcessarSalarioApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessarSalarioApi.class);

  private final RestTemplate restTemplate;

  @Value("${processamento-salarial.base.url}/processa_cabimento")
  private String processarCabimentoUrl;

  @Value("${processamento-salarial.base.url}/autoriza_processo_salario")
  private String autorizarUrl;

  @Value("${processamento-salarial.base.url}/trata_estorno_cabimento")
  private String extornarCabimentoUrl;

  public ProcessarSalarioApi(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public OperationOutcomeResponse processarCabimento(String procSalId, String dataPagamento) {

    var processaCabimento = new ProcessarCabimentoRequest.ProcessaCabimento(procSalId, dataPagamento, "IP");
    var requestBody = new ProcessarCabimentoRequest.RequestBody(processaCabimento);
    var operationRequest = new ProcessarCabimentoRequest.OperationRequest(new RequestHeader(), requestBody);
    var request = new ProcessarCabimentoRequest(operationRequest);

    LOGGER.debug("ProcessarCabimento Request: {}", request);

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    return restTemplate.exchange(
        processarCabimentoUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        OperationOutcomeResponse.class
    ).getBody();
  }

  public OperationOutcomeResponse autorizarSalario(String codigoCabimento, String visa) {

    var autorizaProcessoSalario = new AutorizaSalarioRequest.AutorizaProcessoSalario(
        codigoCabimento,
        visa,                // SIM ou NAO
        "Autorizar Salário",
        "IP",
        "AUTET0001",
        "123456789"
    );
    var requestBody = new AutorizaSalarioRequest.RequestBody(autorizaProcessoSalario);
    var operationRequest = new AutorizaSalarioRequest.OperationRequest(new RequestHeader(), requestBody);
    var request = new AutorizaSalarioRequest(operationRequest);

    LOGGER.debug("AutorizarSalario Request: {}", request);

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    var response = restTemplate.exchange(
        autorizarUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        OperationOutcomeResponse.class
    );

    return response.getBody();
  }

  public OperationOutcomeResponse extornarCabimento(String codigo) {

    var trataEstorno = new EstornoCabimentoRequest.TrataEstornoCabimento("", codigo);
    var requestBody = new EstornoCabimentoRequest.RequestBody(trataEstorno);
    var operationRequest = new EstornoCabimentoRequest.OperationRequest(new RequestHeader(), requestBody);
    var request = new EstornoCabimentoRequest(operationRequest);

    LOGGER.debug("Extornar cabimento Request: {}", request);

    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    var response = restTemplate.exchange(
        extornarCabimentoUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, headers),
        OperationOutcomeResponse.class
    );

    return response.getBody();
  }
}
