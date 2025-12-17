package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateFuncionarioCommandHandlerTest {

    @InjectMocks
    private CreateFuncionarioCommandHandler createFuncionarioCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // CreateFuncionarioCommand command = new CreateFuncionarioCommand(...);
        //
        // When
        // ResponseEntity<FuncionarioResponseDTO> response = createFuncionarioCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
