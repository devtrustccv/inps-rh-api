package cv.inps.rh.funcionario.application.queries;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cv.inps.rh.funcionario.application.dto.WrapperListPagamentosDescontoDTO;
import cv.inps.rh.funcionario.application.service.PagamentosDescontoReadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class GetListPagamentosDescontoQueryHandlerTest {

  @Mock
  private PagamentosDescontoReadService pagamentosDescontoReadService;

  @InjectMocks
  private GetListPagamentosDescontoQueryHandler getListPagamentosDescontoQueryHandler;

  @Test
  void testHandleGetListPagamentosDescontoQuery_delegatesToServiceAndReturnsOk() {
    GetListPagamentosDescontoQuery query = new GetListPagamentosDescontoQuery(
        "11111111-1111-1111-1111-111111111111", "0", "20", null, null, null);
    WrapperListPagamentosDescontoDTO expected = new WrapperListPagamentosDescontoDTO();
    when(pagamentosDescontoReadService.getListPagamentosDesconto(any(GetListPagamentosDescontoQuery.class)))
        .thenReturn(expected);

    ResponseEntity<WrapperListPagamentosDescontoDTO> response =
        getListPagamentosDescontoQueryHandler.handle(query);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expected, response.getBody());
    verify(pagamentosDescontoReadService).getListPagamentosDesconto(query);
  }

}
