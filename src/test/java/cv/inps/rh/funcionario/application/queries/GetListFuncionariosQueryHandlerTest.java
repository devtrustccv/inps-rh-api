package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListFuncionariosQueryHandlerTest {

  @InjectMocks
  private GetListFuncionariosQueryHandler getListFuncionariosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListFuncionariosQuery() {

    // Example:
    // Given
    // GetListFuncionariosQuery query = new GetListFuncionariosQuery(...);
    //
    // When
    // ResponseEntity<WrapperListaFuncionarioDTO> response = getListFuncionariosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
