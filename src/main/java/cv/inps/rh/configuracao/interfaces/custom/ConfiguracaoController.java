package cv.inps.rh.configuracao.interfaces.custom;

import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.constants.ConfigurationType;
import cv.inps.rh.configuracao.domain.service.engine.Configuration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(name = "Configuração", description = "Gestão e Parametrização Global do Sistema")
public class ConfiguracaoController {

  private final Configuration configuration;

  public ConfiguracaoController(Configuration configuration) {
    this.configuration = configuration;
  }

  @PostMapping
  @Operation(
      summary = "Criar nova configuração",
      description = "Cria um novo item de configuração de acordo com o tipo fornecido",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          required = true,
          description = "Dados da configuração a ser criada",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ),
      parameters = {
          @Parameter(
              name = "configurationType",
              description = "Tipo de configuração a ser criada",
              required = true
          )
      }
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "201",
          description = "Configuração criada com sucesso",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
      ),
      @ApiResponse(responseCode = "400", description = "Requisição inválida"),
      @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
  })
  public ResponseEntity<Object> create(
      @RequestBody Object request,
      @RequestParam ConfigurationType configurationType
  ) {
    var data = configuration.create(request, configurationType.getCode());
    return ResponseEntity.status(HttpStatus.CREATED).body(data);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Atualizar uma configuração",
      description = "Atualiza um item de configuração existente."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Atualização bem-sucedida"),
      @ApiResponse(responseCode = "404", description = "Configuração não encontrada"),
      @ApiResponse(responseCode = "400", description = "Dados inválidos"),
  })
  public ResponseEntity<Object> update(
      @Parameter(description = "ID da configuração") @PathVariable String id,
      @RequestBody Object request,
      @Parameter(description = "Tipo de configuração a atualizar") @RequestParam ConfigurationType configurationType
  ) {
    configuration.update(id, request, configurationType.getCode());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{id}")
  @Operation(
      summary = "Consultar uma configuração",
      description = "Obtém os dados de uma configuração específica."
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Consulta bem-sucedida"),
      @ApiResponse(responseCode = "404", description = "Configuração não encontrada"),
  })
  public ResponseEntity<Object> read(
      @Parameter(description = "ID da configuração") @PathVariable String id,
      @Parameter(description = "Tipo de configuração") @RequestParam ConfigurationType configurationType
  ) {
    var data = configuration.read(id, configurationType.getCode());
    return ResponseEntity.ok(data);
  }

  @GetMapping
  @Operation(
      summary = "Listar configurações",
      description = "Retorna uma lista de configurações filtradas por parâmetros e tipo"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
  })
  public ResponseEntity<List<Object>> list(
      @Parameter(description = "Filtros da consulta (dinâmicos)") @RequestParam Map<String, String> params,
      @Parameter(description = "Tipo de configuração a listar") @RequestParam ConfigurationType configurationType
  ) {
    var data = configuration.list(params, configurationType.getCode());
    return ResponseEntity.ok(data);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Remover configuração",
      description = "Exclui uma configuração pelo seu ID"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Configuração removida"),
      @ApiResponse(responseCode = "404", description = "Configuração não encontrada")
  })
  public ResponseEntity<Void> delete(
      @Parameter(description = "ID da configuração") @PathVariable String id,
      @Parameter(description = "Tipo de configuração") @RequestParam ConfigurationType configurationType
  ) {
    configuration.delete(id, configurationType.getCode());
    return ResponseEntity.noContent().build();
  }
}
