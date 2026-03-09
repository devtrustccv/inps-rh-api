package cv.inps.rh.shared.service.model.sniac;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SniacEntriesDTO {

  @JsonProperty("Entry")
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<SniacEntryDTO> entry;
}
