package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetCarreiraByIdQueryHandlerTest {

  @InjectMocks
  private GetCarreiraByIdQueryHandler getCarreiraByIdQueryHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandleGetCarreiraByIdQuery() {
    // Example:
    // Given
    // GetCarreiraByIdQuery query = new GetCarreiraByIdQuery(...);
    //
    // When
    // ResponseEntity<CarreiraResponseDTO> response = getCarreiraByIdQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
