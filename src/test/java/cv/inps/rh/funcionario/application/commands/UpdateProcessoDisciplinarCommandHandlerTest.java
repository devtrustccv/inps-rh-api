package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateProcessoDisciplinarCommandHandlerTest {

  @InjectMocks
  private UpdateProcessoDisciplinarCommandHandler updateProcessoDisciplinarCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {

    // Example:
    // Given
    // UpdateProcessoDisciplinarCommand command = new UpdateProcessoDisciplinarCommand(...);
    //
    // When
    // ResponseEntity<String> response = updateProcessoDisciplinarCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
