package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EliminarCarreiraCommandHandlerTest {

  @InjectMocks
  private EliminarCarreiraCommandHandler eliminarCarreiraCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {
    // Example:
    // Given
    // EliminarCarreiraCommand command = new EliminarCarreiraCommand(...);
    //
    // When
    // ResponseEntity<String> response = eliminarCarreiraCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
