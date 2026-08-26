/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.processamento.interfaces.rest;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.igrp.framework.stereotype.IgrpController;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.commands.CriarSoatCommand;
import cv.inps.rh.processamento.application.commands.FinalizarSoatCommand;
import cv.inps.rh.processamento.application.commands.SalvarDadosApoliceCommand;
import cv.inps.rh.processamento.application.commands.SalvarDadosInstituicaoCommand;
import cv.inps.rh.processamento.application.dto.DadosApoliceRequestDTO;
import cv.inps.rh.processamento.application.dto.DadosApoliceResponseDTO;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoRequestDTO;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoResponseDTO;
import cv.inps.rh.processamento.application.queries.DownloadSoatPdfQuery;
import cv.inps.rh.processamento.application.queries.GetDadosApolicesAtivosQuery;
import cv.inps.rh.processamento.application.queries.GetDadosInstituicaoAtualQuery;
import cv.inps.rh.processamento.application.queries.GetSoatListQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@IgrpController
@RestController
@RequestMapping(path = "processamento/soat")
@Tag(
    name = "Processamento"
)
public class SoatController {


  private final QueryBus queryBus;
  private final CommandBus commandBus;

  public SoatController(QueryBus queryBus, CommandBus commandBus) {
    this.queryBus = queryBus;
    this.commandBus = commandBus;
  }

  @GetMapping(
  )
  @Operation(
      summary = "Get lista SOAT",
      description = "Get lista SOAT",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      implementation = WrapperListDTO.class,
                      type = "object")
              )
          )
      }
  )

  public ResponseEntity<WrapperListDTO> getListaFos(
      @RequestParam(value = "anoReferente", required = false) Integer anoReferente,
      @RequestParam(value = "mesReferente", required = false) Integer mesReferente,
      @RequestParam(value = "page", required = false, defaultValue = "0") String page,
      @RequestParam(value = "size", required = false, defaultValue = "20") String size
  ) {

    final var query = new GetSoatListQuery(anoReferente, mesReferente, Integer.valueOf(page), Integer.valueOf(size));

    return queryBus.handle(query);
  }

  @PostMapping("finalizar")
  @Operation(
      summary = "Finalizar Soat",
      description = "Finalizar Soat",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      type = "String")
              )
          )
      }
  )

  public ResponseEntity<Void> finalizarSoat(@RequestParam(value = "soatId") String soatId) {

    final var command = new FinalizarSoatCommand(soatId);

    return commandBus.send(command);

  }

  @PostMapping("registar")
  @Operation(
      summary = "Registar Soat",
      description = "Registar Soat",
      responses = {
          @ApiResponse(
              responseCode = "200",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(
                      type = "String")
              )
          )
      }
  )
  public ResponseEntity<Void> criarSoat(
      @RequestParam(value = "ano") Integer ano,
      @RequestParam(value = "mes") Integer mes
  ) {

    final var command = new CriarSoatCommand(mes, ano);

    return commandBus.send(command);

  }

    @PostMapping("dados-instituicao")
    @Operation(
            summary = "Create or update institution data",
            description = "Creates the first institution data row or versions the current active row",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DadosInstituicaoResponseDTO.class,
                                            type = "object")
                            )
                    )
            }
    )
    public ResponseEntity<DadosInstituicaoResponseDTO> salvarDadosInstituicao(
            @Valid @RequestBody DadosInstituicaoRequestDTO request) {

        final var command = new SalvarDadosInstituicaoCommand(request);

        return commandBus.send(command);
    }

    @GetMapping("dados-instituicao")
    @Operation(
            summary = "Get current institution data",
            description = "Gets the current active institution data row",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DadosInstituicaoResponseDTO.class,
                                            type = "object")
                            )
                    )
            }
    )
    public ResponseEntity<DadosInstituicaoResponseDTO> getDadosInstituicaoAtual() {

        final var query = new GetDadosInstituicaoAtualQuery();

        return queryBus.handle(query);
  }

    @PostMapping("dados-apolices")
    @Operation(
            summary = "Create or update insurance policy data",
            description = "Creates a policy for an island or versions its current active policy",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DadosApoliceResponseDTO.class,
                                            type = "object")
                            )
                    )
            }
    )
    public ResponseEntity<DadosApoliceResponseDTO> salvarDadosApolice(
            @Valid @RequestBody DadosApoliceRequestDTO request) {

        final var command = new SalvarDadosApoliceCommand(request);

        return commandBus.send(command);
    }

    @GetMapping("dados-apolices")
    @Operation(
            summary = "Get active insurance policies",
            description = "Gets the current active policy for each configured island",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = DadosApoliceResponseDTO.class,
                                            type = "array")
                            )
                    )
            }
    )
    public ResponseEntity<List<DadosApoliceResponseDTO>> getDadosApolicesAtivos() {

        final var query = new GetDadosApolicesAtivosQuery();

        return queryBus.handle(query);
    }

    @GetMapping(value = "ficheiro", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Download SOAT PDF",
            description = "Processes the SOAT Thymeleaf template and downloads the generated PDF",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)
                    )
            }
    )
    public ResponseEntity<byte[]> downloadFicheiroSoat(
            @RequestParam(value = "soatId") String soatId,
            @RequestParam(value = "apoliceId") String apoliceId) {

        return queryBus.handle(new DownloadSoatPdfQuery(soatId, apoliceId));
    }
}
