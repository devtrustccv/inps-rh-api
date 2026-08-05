package cv.inps.rh.assiduidade.interfaces.rest;

import cv.inps.rh.assiduidade.application.dto.RegularizacaoContaRequestDTO;
import cv.inps.rh.assiduidade.application.services.RegularizacaoService;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/assiduidade/regularizacao")
@RequiredArgsConstructor
@Validated
public class RegularizacaoController {

  private final RegularizacaoService regularizacaoService;

  /**
   * Obtém as regularizações pendentes de um funcionário.
   */
  @GetMapping("/funcionario/{funUuid}")
  public List<RegularizacaoContaRequestDTO> getByFunId(
      @PathVariable String funUuid,
      @RequestParam LocalDate dataInicio,
      @RequestParam LocalDate dataFim
  ) {

    return regularizacaoService.getByFunId(funUuid, dataInicio, dataFim);
  }

  /**
   * Cria novas regularizações.
   */
  @PostMapping
  public List<RegularizacaoContaRequestDTO> create(
      @Valid @RequestBody List<RegularizacaoContaRequestDTO> request) {

    return regularizacaoService.create(request);
  }

  /**
   * Atualiza regularizações.
   */
  @PutMapping
  public List<RegularizacaoContaRequestDTO> update(
      @Valid @RequestBody List<RegularizacaoContaRequestDTO> request) {

    return regularizacaoService.update(request);
  }

  /**
   * Valida ou rejeita regularizações.
   * <p>
   * Exemplo:
   * PUT /api/v1/regularizacoes/validate?validation=SIM
   * PUT /api/v1/regularizacoes/validate?validation=NAO
   */
  @PutMapping("/validate")
  public List<RegularizacaoContaRequestDTO> validate(
      @RequestParam EstadoValidacao validation,
      @Valid @RequestBody List<RegularizacaoContaRequestDTO> request) {

    return regularizacaoService.validate(validation.name(), request);
  }

}
