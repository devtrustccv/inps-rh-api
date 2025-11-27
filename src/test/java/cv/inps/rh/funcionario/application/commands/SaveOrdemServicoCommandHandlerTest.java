package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SaveOrdemServicoCommandHandlerTest {

    @InjectMocks
    private SaveOrdemServicoCommandHandler saveOrdemServicoCommandHandler;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testHandle() {
        // Example:
        // Given
        // SaveOrdemServicoCommand command = new SaveOrdemServicoCommand(...);
        //
        // When
        // ResponseEntity<String> response = saveOrdemServicoCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
