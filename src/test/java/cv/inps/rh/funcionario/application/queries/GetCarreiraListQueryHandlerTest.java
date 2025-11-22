package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetCarreiraListQueryHandlerTest {

  @InjectMocks
  private GetCarreiraListQueryHandler getCarreiraListQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetCarreiraListQuery() {

    // Example:
    // Given
    // GetCarreiraListQuery query = new GetCarreiraListQuery(...);
    //
    // When
    // ResponseEntity<WrapperCarreiraListDTO> response = getCarreiraListQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
