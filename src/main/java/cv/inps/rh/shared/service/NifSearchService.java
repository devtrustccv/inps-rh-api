package cv.inps.rh.shared.service;

import cv.inps.rh.shared.service.model.nif.RootResponseDTO;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class NifSearchService {

  private final RestTemplate restTemplate;

  @Value("${external.nif.url}")
  private String url;

  @Value("${external.nif.token}")
  private String token;

  public NifSearchService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public RootResponseDTO getEntries(String name, String numero, Long nif) {

    var headers = new HttpHeaders();
    headers.setBearerAuth(token);

   /*  var finalUrl = UriComponentsBuilder.fromUriString(url)
        .queryParam("nm_contribuinte", name)
        .queryParam("num_id", numero)
        .queryParam("num_nif", nif)
        .toUriString();*/

    var  finalUrl = UriComponentsBuilder.fromUriString(url)
        .queryParam("nm_contribuinte", name)
        .queryParam("num_id", numero)
        .queryParam("num_nif", nif)
        .build()
        .encode(StandardCharsets.UTF_8)   // <-- faz o encode correcto (espaço -> %20)
        .toUri();  

    headers.setContentType(MediaType.APPLICATION_JSON);

    var entity = new HttpEntity<>(headers);

    return restTemplate.exchange(finalUrl, HttpMethod.GET, entity, RootResponseDTO.class).getBody();
  }
}
