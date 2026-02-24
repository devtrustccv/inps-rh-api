package cv.inps.rh.shared.service.model.nif;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RootResponseDTO {

    @JsonProperty("Entries")
    private EntriesDTO entries;
}
