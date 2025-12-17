package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MudarEstadoColaboradorCommandHandlerTest {

    @InjectMocks
    private MudarEstadoColaboradorCommandHandler mudarEstadoColaboradorCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // MudarEstadoColaboradorCommand command = new MudarEstadoColaboradorCommand(...);
        //
        // When
        // ResponseEntity<AtivarInativarColaboradorDTO> response = mudarEstadoColaboradorCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
