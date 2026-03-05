package cv.inps.rh.shared.service.model.sniac;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SniacRootResponseDTO {

    @JsonProperty("Entries")
    private SniacEntriesDTO entries;
}
