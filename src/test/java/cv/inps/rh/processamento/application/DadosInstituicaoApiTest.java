package cv.inps.rh.processamento.application;

import cv.igrp.framework.core.domain.CommandBus;
import cv.igrp.framework.core.domain.QueryBus;
import cv.inps.rh.processamento.application.commands.SalvarDadosInstituicaoCommand;
import cv.inps.rh.processamento.application.commands.SalvarDadosInstituicaoCommandHandler;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoRequestDTO;
import cv.inps.rh.processamento.application.dto.DadosInstituicaoResponseDTO;
import cv.inps.rh.processamento.application.queries.GetDadosInstituicaoAtualQuery;
import cv.inps.rh.processamento.application.queries.GetDadosInstituicaoAtualQueryHandler;
import cv.inps.rh.processamento.domain.service.SoatService;
import cv.inps.rh.processamento.interfaces.rest.SoatController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadosInstituicaoApiTest {

  @Mock
  private SoatService service;

  @Mock
  private CommandBus commandBus;

  @Mock
  private QueryBus queryBus;

  private SalvarDadosInstituicaoCommandHandler commandHandler;
  private GetDadosInstituicaoAtualQueryHandler queryHandler;
  private SoatController controller;

  @BeforeEach
  void setUp() {
    commandHandler = new SalvarDadosInstituicaoCommandHandler(service);
    queryHandler = new GetDadosInstituicaoAtualQueryHandler(service);
    controller = new SoatController(queryBus, commandBus);
  }

  @Test
  void commandHandlerShouldSaveAndReturnDadosInstituicao() {
    var request = mock(DadosInstituicaoRequestDTO.class);
    var response = mock(DadosInstituicaoResponseDTO.class);
    when(service.salvarDadosInstituicao(request)).thenReturn(response);

    var result = commandHandler.handle(new SalvarDadosInstituicaoCommand(request));

    assertSame(response, result.getBody());
    verify(service).salvarDadosInstituicao(request);
  }

  @Test
  void queryHandlerShouldReturnCurrentDadosInstituicao() {
    var response = mock(DadosInstituicaoResponseDTO.class);
    when(service.obterDadosInstituicaoAtual()).thenReturn(response);

    var result = queryHandler.handle(new GetDadosInstituicaoAtualQuery());

    assertSame(response, result.getBody());
    verify(service).obterDadosInstituicaoAtual();
  }

  @Test
  void controllerShouldDispatchSaveCommand() {
    var request = mock(DadosInstituicaoRequestDTO.class);
    var response = mock(DadosInstituicaoResponseDTO.class);
    when(commandBus.send(any(SalvarDadosInstituicaoCommand.class)))
        .thenReturn(ResponseEntity.ok(response));

    var result = controller.salvarDadosInstituicao(request);

    var captor = ArgumentCaptor.forClass(SalvarDadosInstituicaoCommand.class);
    verify(commandBus).send(captor.capture());
    assertSame(request, captor.getValue().getDadosInstituicaoRequest());
    assertSame(response, result.getBody());
  }

  @Test
  void controllerShouldDispatchCurrentQuery() {
    var response = mock(DadosInstituicaoResponseDTO.class);
    when(queryBus.handle(any(GetDadosInstituicaoAtualQuery.class)))
        .thenReturn(ResponseEntity.ok(response));

    var result = controller.getDadosInstituicaoAtual();

    verify(queryBus).handle(any(GetDadosInstituicaoAtualQuery.class));
    assertSame(response, result.getBody());
  }
}
