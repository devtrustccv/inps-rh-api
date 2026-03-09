package cv.inps.rh.shared.service;

import cv.inps.rh.shared.service.model.sniac.SniacRequestBodyDTO;
import cv.inps.rh.shared.service.model.sniac.SniacRootResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SniacSearchService {

    private final RestTemplate restTemplate;

    @Value("${external.sniac.url}")
    private String url;

    @Value("${external.sniac.token}")
    private String token;

    public SniacSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SniacRootResponseDTO getEntries(String nomeCompleto, String dataNasc, String nic) {

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var requestBody = new SniacRequestBodyDTO(
                new SniacRequestBodyDTO.PesquisaDocumentoSNIAC(nomeCompleto, dataNasc, nic)
        );

        var entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, SniacRootResponseDTO.class).getBody();
    }
}
