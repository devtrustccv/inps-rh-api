package cv.inps.rh.emprestimo.application.dto.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cv.inps.rh.shared.util.NumberUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PlanoFinanceiroSerializer extends JsonSerializer<BigDecimal> {

  private static final DecimalFormat US_DECIMAL_FORMAT = NumberUtils.spaceDecimalFormat();

  @Override
  public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    gen.writeString(US_DECIMAL_FORMAT.format(value));
  }
}
