/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.domain.service.IConfiguration;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(name = "Configuracao", description = "Parameterização Global")
public class ConfiguracaoTestController {

  private final IConfiguration configuration;

  public ConfiguracaoTestController(IConfiguration configuration) {
    this.configuration = configuration;
  }

  @PostMapping
  public ResponseEntity<Object> create(@RequestBody Object request, @RequestParam String configurationType) {
    return ResponseEntity.ok(configuration.create(request, configurationType));
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Object request, @RequestParam String configurationType) {
    return ResponseEntity.ok(configuration.update(id, request, configurationType));
  }

  @GetMapping
  public ResponseEntity<List<Object>> list(@RequestParam Map<String, String> params, @RequestParam String configurationType) {
    return ResponseEntity.ok(configuration.list(params, configurationType));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id, @RequestParam String configurationType) {
    configuration.delete(id, configurationType);
    return ResponseEntity.ok().build();
  }

}
