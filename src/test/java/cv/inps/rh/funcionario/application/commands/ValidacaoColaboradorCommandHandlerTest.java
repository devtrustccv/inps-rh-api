package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidacaoColaboradorCommandHandlerTest {

    @InjectMocks
    private ValidacaoColaboradorCommandHandler validacaoColaboradorCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // ValidacaoColaboradorCommand command = new ValidacaoColaboradorCommand(...);
        //
        // When
        // ResponseEntity<AtivarInativarColaboradorDTO> response = validacaoColaboradorCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
