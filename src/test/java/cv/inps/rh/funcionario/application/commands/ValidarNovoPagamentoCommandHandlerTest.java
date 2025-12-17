package cv.inps.rh.funcionario.application.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidarNovoPagamentoCommandHandlerTest {

    @InjectMocks
    private ValidarNovoPagamentoCommandHandler validarNovoPagamentoCommandHandler;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testHandle() {

        // Example:
        // Given
        // ValidarNovoPagamentoCommand command = new ValidarNovoPagamentoCommand(...);
        //
        // When
        // ResponseEntity<String> response = validarNovoPagamentoCommandHandler.handle(command);
        //
        // Then
        // assertNotNull(response);
        // assertEquals(..., response.getBody());
    }
}
