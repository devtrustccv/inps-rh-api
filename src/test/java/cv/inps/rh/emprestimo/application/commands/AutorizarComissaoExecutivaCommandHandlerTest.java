package cv.inps.rh.emprestimo.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AutorizarComissaoExecutivaCommandHandlerTest {

    @InjectMocks
    private AutorizarComissaoExecutivaCommandHandler autorizarComissaoExecutivaCommandHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHandle() {
        // Example:
        // Given
        // AutorizarComissaoExecutivaCommand command = new AutorizarComissaoExecutivaCommand(...);
        //
        // When
        // ResponseEntity<String> response = autorizarComissaoExecutivaCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
