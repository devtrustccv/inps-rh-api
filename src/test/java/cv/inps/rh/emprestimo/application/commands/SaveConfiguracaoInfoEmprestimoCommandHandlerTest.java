package cv.inps.rh.emprestimo.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaveConfiguracaoInfoEmprestimoCommandHandlerTest {

  @InjectMocks
  private SaveConfiguracaoInfoEmprestimoCommandHandler saveConfiguracaoInfoEmprestimoCommandHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandle() {

    // Example:
    // Given
    // SaveConfiguracaoInfoEmprestimoCommand command = new SaveConfiguracaoInfoEmprestimoCommand(...);
    //
    // When
    // ResponseEntity<String> response = saveConfiguracaoInfoEmprestimoCommandHandler.handle(command);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }
}
