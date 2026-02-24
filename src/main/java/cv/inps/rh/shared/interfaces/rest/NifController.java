package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.service.NifSearchService;
import cv.inps.rh.shared.service.model.nif.RootResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nif/search")
@Tag(name = "NIF", description = "Pesquisa NIF")
@RequiredArgsConstructor
public class NifController {

  private final NifSearchService service;

  @GetMapping
  public RootResponseDTO get(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String numero,
      @RequestParam(required = false) Long nif) {
    return service.getEntries(name, numero, nif);
  }
}
