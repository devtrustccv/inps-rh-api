package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListMobilidadesQueryHandlerTest {

  @InjectMocks
  private GetListMobilidadesQueryHandler getListMobilidadesQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListMobilidadesQuery() {

    // Example:
    // Given
    // GetListMobilidadesQuery query = new GetListMobilidadesQuery(...);
    //
    // When
    // ResponseEntity<WrapperListMobilidadeDTO> response = getListMobilidadesQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
