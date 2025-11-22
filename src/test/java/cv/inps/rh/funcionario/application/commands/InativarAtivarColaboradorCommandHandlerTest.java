package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class InativarAtivarColaboradorCommandHandlerTest {

    @InjectMocks
    private InativarAtivarColaboradorCommandHandler inativarAtivarColaboradorCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // InativarAtivarColaboradorCommand command = new InativarAtivarColaboradorCommand(...);
        //
        // When
        // ResponseEntity<AtivarInativarColaboradorDTO> response = inativarAtivarColaboradorCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
