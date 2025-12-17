package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListContratosQueryHandlerTest {

  @InjectMocks
  private GetListContratosQueryHandler getListContratosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListContratosQuery() {

    // Example:
    // Given
    // GetListContratosQuery query = new GetListContratosQuery(...);
    //
    // When
    // ResponseEntity<WrapperListContratoDTO> response = getListContratosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
