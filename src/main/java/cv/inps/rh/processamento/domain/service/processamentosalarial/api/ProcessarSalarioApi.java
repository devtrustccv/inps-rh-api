package cv.inps.rh.processamento.domain.service.processamentosalarial.api;

import cv.inps.rh.processamento.domain.service.processamentosalarial.api.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ProcessarSalarioApi {

  private final RestTemplate restTemplate;

  @Value("${processamento-salarial.base.url}/processa_cabimento")
  private String processarCabimentoUrl;

  @Value("${processamento-salarial.base.url}/autoriza_processo_salario")
  private String autorizarUrl;

  @Value("${processamento-salarial.base.url}/trata_estorno_cabimento")
  private String extornarCabimentoUrl;

  @Value("${processamento-salarial.token}")
  private String token;

  @Getter
  @Value("${processamento-salarial.cabimentacao}")
  private boolean enableCabimentation;

  public ProcessarSalarioApi(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public OperationOutcomeResponse processarCabimento(String procSalId, String dataPagamento) {

    var processaCabimento = new ProcessarCabimentoRequest.ProcessaCabimento(procSalId, dataPagamento, "IP");
    var requestBody = new ProcessarCabimentoRequest.RequestBody(processaCabimento);
    var operationRequest = new ProcessarCabimentoRequest.OperationRequest(new RequestHeader(), requestBody);
    var request = new ProcessarCabimentoRequest(operationRequest);

    log.debug("ProcessarCabimento Request: {}", request);

    return restTemplate.exchange(
        processarCabimentoUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, jsonHeaders()),
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

    log.debug("AutorizarSalario Request: {}", request);

    return restTemplate.exchange(
        autorizarUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, jsonHeaders()),
        OperationOutcomeResponse.class
    ).getBody();
  }

  public OperationOutcomeResponse extornarCabimento(String codigo) {

    var trataEstorno = new EstornoCabimentoRequest.TrataEstornoCabimento("", codigo);
    var requestBody = new EstornoCabimentoRequest.RequestBody(trataEstorno);
    var operationRequest = new EstornoCabimentoRequest.OperationRequest(new RequestHeader(), requestBody);
    var request = new EstornoCabimentoRequest(operationRequest);

    log.debug("Extornar cabimento Request: {}", request);

    return restTemplate.exchange(
        extornarCabimentoUrl,
        HttpMethod.POST,
        new HttpEntity<>(request, jsonHeaders()),
        OperationOutcomeResponse.class
    ).getBody();
  }

  private HttpHeaders jsonHeaders() {
    var headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
