package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteProcessoDisciplinarCommandHandlerTest {

  @InjectMocks
  private DeleteProcessoDisciplinarCommandHandler deleteProcessoDisciplinarCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {
    // Example:
    // Given
    // DeleteProcessoDisciplinarCommand command = new DeleteProcessoDisciplinarCommand(...);
    //
    // When
    // ResponseEntity<String> response = deleteProcessoDisciplinarCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
