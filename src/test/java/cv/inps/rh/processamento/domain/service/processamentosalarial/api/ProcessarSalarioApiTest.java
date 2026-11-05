package cv.inps.rh.processamento.domain.service.processamentosalarial.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ProcessarSalarioApiTest {

  private static final String URL = "https://3sb.inps.cv/integracao_darh/v1.0.0/processa_cabimento";

  private static final String EXPECTED_BODY = """
      {
        "OperationRequest": {
          "RequestHeader": {
            "Autorization": {
              "UserName": "sips",
              "Password": "sips@is",
              "CanalCode": "1001",
              "RolesName": "sips"
            },
            "User": {
              "UserID": 0
            }
          },
          "RequestBody": {
            "processa_cabimento": {
              "p_proc_sal_id": "241843",
              "p_data_pag": "30-09-2025",
              "p_sessid": "IP"
            }
          }
        }
      }
      """;

  private static final String OUTCOME = """
      {
        "content": {
          "resourceType": "OperationOutcome",
          "issue": {
            "code": 200,
            "diagnostics": "OK - processamento definitivo executado."
          }
        }
      }
      """;

  @Test
  void processarCabimentoEnviaOContratoDoServicoComBearerToken() {
    var restTemplate = new RestTemplate();
    var server = MockRestServiceServer.bindTo(restTemplate).build();
    var api = new ProcessarSalarioApi(restTemplate);
    ReflectionTestUtils.setField(api, "processarCabimentoUrl", URL);
    ReflectionTestUtils.setField(api, "token", "token-de-teste");

    server.expect(requestTo(URL))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-de-teste"))
        .andExpect(content().json(EXPECTED_BODY, true))
        .andRespond(withSuccess(OUTCOME, MediaType.APPLICATION_JSON));

    var outcome = api.processarCabimento("241843", "30-09-2025");

    server.verify();
    assertThat(outcome.content().issue().code()).isEqualTo(200);
  }
}
