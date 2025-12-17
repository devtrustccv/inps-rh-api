package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListRegimesQueryHandlerTest {

  @InjectMocks
  private GetListRegimesQueryHandler getListRegimesQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListRegimesQuery() {

    // Example:
    // Given
    // GetListRegimesQuery query = new GetListRegimesQuery(...);
    //
    // When
    // ResponseEntity<WrapperRegimeListDTO> response = getListRegimesQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
