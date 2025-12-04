package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NovoPedidoDeclaracaoCommandHandlerTest {

  @InjectMocks
  private NovoPedidoDeclaracaoCommandHandler novoPedidoDeclaracaoCommandHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandle() {

    // Example:
    // Given
    // NovoPedidoDeclaracaoCommand command = new NovoPedidoDeclaracaoCommand(...);
    //
    // When
    // ResponseEntity<String> response = novoPedidoDeclaracaoCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
