package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetTiposDocumentoAtivosQueryHandlerTest {

  @InjectMocks
  private GetTiposDocumentoAtivosQueryHandler getTiposDocumentoAtivosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetTiposDocumentoAtivosQuery() {

    // Example:
    // Given
    // GetTiposDocumentoAtivosQuery query = new GetTiposDocumentoAtivosQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getTiposDocumentoAtivosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
