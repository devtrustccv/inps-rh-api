package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AtualizarCarreiraCommandHandlerTest {

  @InjectMocks
  private AtualizarCarreiraCommandHandler atualizarCarreiraCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {
    // Example:
    // Given
    // AtualizarCarreiraCommand command = new AtualizarCarreiraCommand(...);
    //
    // When
    // ResponseEntity<String> response = atualizarCarreiraCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
