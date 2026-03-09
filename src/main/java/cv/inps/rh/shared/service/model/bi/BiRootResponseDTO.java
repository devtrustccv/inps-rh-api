package cv.inps.rh.shared.service.model.bi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BiRootResponseDTO {
    @JsonProperty("Entries")
    private BiEntriesDTO entries;
}
