package cv.inps.rh.shared.service;

import cv.inps.rh.shared.service.model.bi.BiRootResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class BiSearchService {

    private final RestTemplate restTemplate;

    @Value("${external.bi.url}")
    private String url;

    @Value("${external.bi.token}")
    private String token;

    public BiSearchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BiRootResponseDTO getEntries(String bi) {

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var finalUrl = UriComponentsBuilder.fromUriString(url)
                .queryParam("p_num_bi", bi)
                .toUriString();

        var entity = new HttpEntity<>(headers);

        return restTemplate.exchange(finalUrl, HttpMethod.GET, entity, BiRootResponseDTO.class).getBody();
    }
}
