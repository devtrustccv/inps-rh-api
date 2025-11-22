package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetCargosAtivosQueryHandlerTest {

  @InjectMocks
  private GetCargosAtivosQueryHandler getCargosAtivosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetCargosAtivosQuery() {

    // Example:
    // Given
    // GetCargosAtivosQuery query = new GetCargosAtivosQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getCargosAtivosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
