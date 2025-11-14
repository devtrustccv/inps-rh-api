/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.interfaces.custom;

import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.TipoContratoLaboralResponseDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralRequestDTO;
import cv.inps.rh.configuracao.application.dto.VinculoLaboralResponseDTO;
import cv.inps.rh.configuracao.domain.service.configurationengine.IConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(name = "Configuracao", description = "Parameterização Global")
public class ConfiguracaoController {

  private final IConfiguration configuration;

  public ConfiguracaoController(IConfiguration configuration) {
    this.configuration = configuration;
  }

  @PostMapping
  @Operation(
      summary = "Create configuration",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  oneOf = {
                      TipoContratoLaboralRequestDTO.class,
                      VinculoLaboralRequestDTO.class
                  }
              )
          )
      )
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Success",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  oneOf = {
                      TipoContratoLaboralResponseDTO.class,
                      VinculoLaboralResponseDTO.class
                  }
              )
          )
      )
  })
  public ResponseEntity<Object> create(
      @RequestBody Map<String, Object> request,
      @RequestParam String configurationType
  ) {
    var data = configuration.create(request, configurationType);
    return ResponseEntity.status(HttpStatus.CREATED).body(data);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Object request, @RequestParam String configurationType) {
    var data = configuration.update(id, request, configurationType);
    return ResponseEntity.ok(data);
  }

  @GetMapping
  public ResponseEntity<List<Object>> list(@RequestParam Map<String, String> params, @RequestParam String configurationType) {
    var data = configuration.list(params, configurationType);
    return ResponseEntity.ok(data);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id, @RequestParam String configurationType) {
    configuration.delete(id, configurationType);
    return ResponseEntity.noContent().build();
  }

}
