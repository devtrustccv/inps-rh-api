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

    var s = dbData.trim();
    try {
      // formato padrão com hífens
      if (s.length() == 36) {
        if (!s.matches("(?i)[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
          return null;
        }
        return UUID.fromString(s);
      }
      // string hex de 32 caracteres (sem hífens)
      if (s.length() == 32 && s.matches("(?i)[0-9a-f]{32}")) {
        s = s.substring(0, 8) + "-" +
            s.substring(8, 12) + "-" +
            s.substring(12, 16) + "-" +
            s.substring(16, 20) + "-" +
            s.substring(20, 32);
        return UUID.fromString(s);
      }
    } catch (IllegalArgumentException ex) {
      return null;
    }

    return null;
  }
}
