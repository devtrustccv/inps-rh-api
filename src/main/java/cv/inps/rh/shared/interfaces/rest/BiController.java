package cv.inps.rh.shared.interfaces.rest;

import cv.inps.rh.shared.service.BiSearchService;
import cv.inps.rh.shared.service.model.bi.BiRootResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bi/search")
@Tag(name = "BI", description = "Pesquisa BI")
@RequiredArgsConstructor
public class BiController {

    private final BiSearchService service;

    @GetMapping
    public BiRootResponseDTO searchBi(@RequestParam String bi) {
        return service.getEntries(bi);
    }
}
