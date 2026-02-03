package cv.inps.rh.emprestimo.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetConfiguracaoEmprestimoQueryHandlerTest {

  @InjectMocks
  private GetConfiguracaoEmprestimoQueryHandler getConfiguracaoEmprestimoQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetConfiguracaoEmprestimoQuery() {

    // Example:
    // Given
    // GetConfiguracaoEmprestimoQuery query = new GetConfiguracaoEmprestimoQuery(...);
    //
    // When
    // ResponseEntity<List<InformacaoEmprestimoRequestDTO>> response = getConfiguracaoEmprestimoQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
