package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListPagamentosDescontoQueryHandlerTest {

  @InjectMocks
  private GetListPagamentosDescontoQueryHandler getListPagamentosDescontoQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListPagamentosDescontoQuery() {

    // Example:
    // Given
    // GetListPagamentosDescontoQuery query = new GetListPagamentosDescontoQuery(...);
    //
    // When
    // ResponseEntity<WrapperListPagamentosDescontoDTO> response = getListPagamentosDescontoQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
