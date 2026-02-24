package cv.inps.rh.assiduidade.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorasDispensaStatusDTO {
    private String horasDisponiveis;
    private String horasUsadas;
}
