package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetProcessoDisciplinarByIdQueryHandlerTest {

  @InjectMocks
  private GetProcessoDisciplinarByIdQueryHandler getProcessoDisciplinarByIdQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetProcessoDisciplinarByIdQuery() {
    // Example:
    // Given
    // GetProcessoDisciplinarByIdQuery query = new GetProcessoDisciplinarByIdQuery(...);
    //
    // When
    // ResponseEntity<ProcessoDisciplinarResponseDTO> response = getProcessoDisciplinarByIdQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
