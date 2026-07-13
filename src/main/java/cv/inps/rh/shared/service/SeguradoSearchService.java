package cv.inps.rh.shared.service;

import cv.inps.rh.shared.service.model.segurado.SeguradoRequestDTO;
import cv.inps.rh.shared.service.model.segurado.SeguradoResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SeguradoSearchService {

  private final RestTemplate restTemplate;

  @Value("${external.segurado.url}")
  private String url;

  @Value("${external.segurado.token}")
  private String token;

  public SeguradoSearchService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public SeguradoResponseDTO getSegurado(SeguradoRequestDTO request) {

    var headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_XML));

    var entity = new HttpEntity<>(request, headers);

    return restTemplate.exchange(
        url,
        HttpMethod.POST,
        entity,
        SeguradoResponseDTO.class
    ).getBody();
  }
}
