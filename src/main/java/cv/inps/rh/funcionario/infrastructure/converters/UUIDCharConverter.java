package cv.inps.rh.funcionario.infrastructure.converters;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Converter(autoApply = true)
public class UUIDCharConverter implements AttributeConverter<UUID, String> {

  @Override
  public String convertToDatabaseColumn(UUID attribute) {
    //System.out.println("convertToDatabaseColumn: " + attribute);
    return attribute != null ? attribute.toString() : null;
  }

  @Override
  public UUID convertToEntityAttribute(String dbData) {
    //System.out.println("convertToEntityAttribute: " + dbData);

    if (!StringUtils.hasText(dbData)) {
      return null;
    }

    // RAW(16) armazenado como hex string sem hífens
   /* if (dbData.length() == 32 && !dbData.contains("-")) {
      dbData = dbData.substring(0, 8) + "-" +
          dbData.substring(8, 12) + "-" +
          dbData.substring(12, 16) + "-" +
          dbData.substring(16, 20) + "-" +
          dbData.substring(20, 32);
    }*/

    return UUID.fromString(dbData);
  }
}
