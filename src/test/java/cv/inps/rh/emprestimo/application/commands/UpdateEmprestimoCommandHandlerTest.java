package cv.inps.rh.emprestimo.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateEmprestimoCommandHandlerTest {

    @InjectMocks
    private UpdateEmprestimoCommandHandler updateEmprestimoCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // UpdateEmprestimoCommand command = new UpdateEmprestimoCommand(...);
        //
        // When
        // ResponseEntity<String> response = updateEmprestimoCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
