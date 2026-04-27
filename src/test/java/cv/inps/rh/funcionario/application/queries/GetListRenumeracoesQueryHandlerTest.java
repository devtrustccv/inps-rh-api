package cv.inps.rh.funcionario.application.queries;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.service.remuneracao.RenumeracoesReadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class GetListRenumeracoesQueryHandlerTest {

  @Mock
  private RenumeracoesReadService renumeracoesReadService;

  @InjectMocks
  private GetListRenumeracoesQueryHandler getListRenumeracoesQueryHandler;

  @Test
  void testHandleGetListRenumeracoesQuery_delegatesToServiceAndReturnsOk() {
    GetListRenumeracoesQuery query = new GetListRenumeracoesQuery(
        "11111111-1111-1111-1111-111111111111", "20", "0", null, null, null);
    WrapperListRenumeracaoDTO expected = new WrapperListRenumeracaoDTO();
    when(renumeracoesReadService.getListRenumeracoes(any(GetListRenumeracoesQuery.class)))
        .thenReturn(expected);

    ResponseEntity<WrapperListRenumeracaoDTO> response = getListRenumeracoesQueryHandler.handle(query);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expected, response.getBody());
    verify(renumeracoesReadService).getListRenumeracoes(query);
  }

}
