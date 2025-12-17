package cv.inps.rh.funcionario.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListRenumeracoesQueryHandlerTest {

  @InjectMocks
  private GetListRenumeracoesQueryHandler getListRenumeracoesQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetListRenumeracoesQuery() {

    // Example:
    // Given
    // GetListRenumeracoesQuery query = new GetListRenumeracoesQuery(...);
    //
    // When
    // ResponseEntity<WrapperListAbonoSubsidiosDTO> response = getListRenumeracoesQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
