package cv.inps.rh.shared.service;

import cv.inps.rh.shared.service.model.nif.RootResponseDTO;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Optional;

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

    var  finalUrl = UriComponentsBuilder.fromUriString(url)
        .queryParam("nm_contribuinte", Optional.ofNullable(normalizeName(name)))
        .queryParam("num_id", Optional.ofNullable(numero))
        .queryParam("num_nif",Optional.ofNullable(nif))
        .build()
        .encode(StandardCharsets.UTF_8)   // <-- faz o encode correcto (espaço -> %20)
        .toUri();  

    headers.setContentType(MediaType.APPLICATION_JSON);

    var entity = new HttpEntity<>(headers);

    return restTemplate.exchange(finalUrl, HttpMethod.GET, entity, RootResponseDTO.class).getBody();
  }


  

    public static String normalizeName(String name) {
        if (name == null) {
            return null;
        }

        // remove espaços no início/fim e colapsa espaços múltiplos em um só
        String normalized = name.trim().replaceAll("\\s+", " ");

        // remove acentos (á, ã, ç, é... -> a, a, c, e...)
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return normalized;
    }
}
