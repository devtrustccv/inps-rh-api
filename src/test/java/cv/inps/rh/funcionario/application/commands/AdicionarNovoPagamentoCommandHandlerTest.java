package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdicionarNovoPagamentoCommandHandlerTest {

    @InjectMocks
    private AdicionarNovoPagamentoCommandHandler adicionarNovoPagamentoCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // AdicionarNovoPagamentoCommand command = new AdicionarNovoPagamentoCommand(...);
        //
        // When
        // ResponseEntity<String> response = adicionarNovoPagamentoCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
