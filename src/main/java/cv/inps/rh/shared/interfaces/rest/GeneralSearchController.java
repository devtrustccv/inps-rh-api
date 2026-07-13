package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.service.BiSearchService;
import cv.inps.rh.shared.service.NifSearchService;
import cv.inps.rh.shared.service.SeguradoSearchService;
import cv.inps.rh.shared.service.SniacSearchService;
import cv.inps.rh.shared.service.model.bi.BiRootResponseDTO;
import cv.inps.rh.shared.service.model.nif.RootResponseDTO;
import cv.inps.rh.shared.service.model.segurado.SeguradoRequestDTO;
import cv.inps.rh.shared.service.model.segurado.SeguradoResponseDTO;
import cv.inps.rh.shared.service.model.sniac.SniacRootResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@Tag(name = "Pesquisa Global", description = "Search BI/Segurado/SNIAC")
@RequiredArgsConstructor
public class GeneralSearchController {

  private final BiSearchService biSearchService;
  private final NifSearchService nifSearchService;
  private final SniacSearchService sniacSearchService;
  private final SeguradoSearchService seguradoSearchService;

  @GetMapping("/api/bi/search")
  public BiRootResponseDTO searchBi(@RequestParam String bi) {
    return biSearchService.getEntries(bi);
  }

  @GetMapping("/api/nif/search")
  public RootResponseDTO getNif(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String numero,
      @RequestParam(required = false) Long nif) {
    return nifSearchService.getEntries(name, numero, nif);
  }

  @GetMapping("/api/sniac/search")
  public SniacRootResponseDTO getSniac(
      @RequestParam(required = false) String nomeCompleto,
      @RequestParam(required = false) String dataNasc,
      @RequestParam(required = false) String nic) {
    return sniacSearchService.getEntries(nomeCompleto, dataNasc, nic);
  }

  @PostMapping("/api/segurado/search")
  public ResponseEntity<SeguradoResponseDTO> search(@RequestBody SeguradoRequestDTO request) {

    var response = seguradoSearchService.getSegurado(request);

    return ResponseEntity.ok(response);
  }
}
