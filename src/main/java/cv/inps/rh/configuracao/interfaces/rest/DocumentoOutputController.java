/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.configuracao.interfaces.rest;

import cv.igrp.framework.stereotype.IgrpController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import cv.igrp.framework.core.domain.QueryBus;
import cv.inps.rh.configuracao.application.queries.*;
import cv.igrp.framework.core.domain.CommandBus;
import cv.inps.rh.configuracao.application.commands.*;
import cv.inps.rh.configuracao.application.dto.WrapperDocOutputListDTO;
import cv.inps.rh.configuracao.application.dto.DocOutputResponseDTO;
import cv.inps.rh.configuracao.application.dto.DocOutputRequestDTO;
import java.util.Map;

@IgrpController
@RestController
@RequestMapping(path = "configuracao")
@Tag(
    name = "Configuracao",
    description = "gest"
)
public class DocumentoOutputController {

  
  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public DocumentoOutputController(QueryBus queryBus, CommandBus commandBus) {
          this.queryBus = queryBus;
          this.commandBus = commandBus;
  }
   @GetMapping(
   value = "documentos-output"
  )
  @Operation(
    summary = "Get output documents",
    description = "Get output documents",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = WrapperDocOutputListDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<WrapperDocOutputListDTO> getOutputDocuments(
    @RequestParam(value = "tipoDocumento", required = false) String tipoDocumento,
    @RequestParam(value = "pageNumber", required = false, defaultValue = "0") String pageNumber,
    @RequestParam(value = "pageSize", required = false, defaultValue = "20") String pageSize)
  {

      final var query = new GetOutputDocumentsQuery(tipoDocumento, pageNumber, pageSize);

      return queryBus.handle(query);

  }

   @GetMapping(
   value = "documentos-output/{id}"
  )
  @Operation(
    summary = "Get output document by id",
    description = "Get output document by id",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = DocOutputResponseDTO.class,
                  type = "object")
          )
      )
    }
  )
  
  public ResponseEntity<DocOutputResponseDTO> getOutputDocumentById(
    @PathVariable(value = "id") String id)
  {

      final var query = new GetOutputDocumentByIdQuery(id);

      return queryBus.handle(query);

  }

   @PostMapping(
   value = "documentos-output"
  )
  @Operation(
    summary = "Save output document",
    description = "Save output document",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> saveOutputDocument(@Valid @RequestBody DocOutputRequestDTO saveOutputDocumentRequest
    )
  {

      final var command = new SaveOutputDocumentCommand(saveOutputDocumentRequest);

      return commandBus.send(command);

  }

   @PutMapping(
   value = "documentos-output/{id}"
  )
  @Operation(
    summary = "Update output document",
    description = "Update output document",
    responses = {
      @ApiResponse(
          responseCode = "200",
          
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(
                  implementation = String.class,
                  type = "String")
          )
      )
    }
  )
  
  public ResponseEntity<Map<String, ?>> updateOutputDocument(@Valid @RequestBody DocOutputRequestDTO updateOutputDocumentRequest
    , @PathVariable(value = "id") String id)
  {

      final var command = new UpdateOutputDocumentCommand(updateOutputDocumentRequest, id);

      return commandBus.send(command);

  }

}