package cv.inps.rh.processamento.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ExecutarAcaoNoProcessamentoCommandHandlerTest {

  @InjectMocks
  private ExecutarAcaoNoProcessamentoCommandHandler executarAcaoNoProcessamentoCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {

    // Example:
    // Given
    // ExecutarAcaoNoProcessamentoCommand command = new ExecutarAcaoNoProcessamentoCommand(...);
    //
    // When
    // ResponseEntity<String> response = executarAcaoNoProcessamentoCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
