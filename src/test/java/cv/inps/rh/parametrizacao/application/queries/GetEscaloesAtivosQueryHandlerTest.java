package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetEscaloesAtivosQueryHandlerTest {

  @InjectMocks
  private GetEscaloesAtivosQueryHandler getEscaloesAtivosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetEscaloesAtivosQuery() {

    // Example:
    // Given
    // GetEscaloesAtivosQuery query = new GetEscaloesAtivosQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getEscaloesAtivosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
