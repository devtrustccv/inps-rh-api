package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetValicoesUtilizadoresQueryHandlerTest {

  @InjectMocks
  private GetValicoesUtilizadoresQueryHandler getValicoesUtilizadoresQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetValicoesUtilizadoresQuery() {

    // Example:
    // Given
    // GetValicoesUtilizadoresQuery query = new GetValicoesUtilizadoresQuery(...);
    //
    // When
    // ResponseEntity<WrapperListaValidacoesDTO> response = getValicoesUtilizadoresQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
