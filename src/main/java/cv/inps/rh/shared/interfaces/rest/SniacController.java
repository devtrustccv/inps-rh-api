package cv.inps.rh.shared.interfaces.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cv.inps.rh.shared.service.SniacSearchService;
import cv.inps.rh.shared.service.model.sniac.SniacRootResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sniac/search")
@Tag(name = "SNIAC", description = "Pesquisa SNIAC")
@RequiredArgsConstructor
public class SniacController {

    private final SniacSearchService service;

    @GetMapping
    public SniacRootResponseDTO get(
            @RequestParam(required = false) String nomeCompleto,
            @RequestParam(required = false) String dataNasc,
            @RequestParam(required = false) String nic) {
        return service.getEntries(nomeCompleto, dataNasc, nic);
    }
}
