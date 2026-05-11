package cv.inps.rh.funcionario.application.queries;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cv.inps.rh.funcionario.application.dto.WrapperListReciboDTO;
import cv.inps.rh.funcionario.application.service.recibo.ReciboReadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class GetListRecibosQueryHandlerTest {

  @Mock
  private ReciboReadService reciboReadService;

  @InjectMocks
  private GetListRecibosQueryHandler getListRecibosQueryHandler;

  @Test
  void testHandleGetListRecibosQuery_delegatesToServiceAndReturnsOk() {
    GetListRecibosQuery query = new GetListRecibosQuery(
        "11111111-1111-1111-1111-111111111111", "20", "0", null, null);
    WrapperListReciboDTO expected = new WrapperListReciboDTO();
    when(reciboReadService.getListRecibos(any(GetListRecibosQuery.class)))
        .thenReturn(expected);

    ResponseEntity<WrapperListReciboDTO> response = getListRecibosQueryHandler.handle(query);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertSame(expected, response.getBody());
    verify(reciboReadService).getListRecibos(query);
  }

}
