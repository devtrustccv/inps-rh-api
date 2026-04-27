package cv.inps.rh.shared.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public final class DateFormatter {

  public static final DateTimeFormatter EXTENDED_DATE_PT = DateTimeFormatter.ofPattern(
      "dd 'de' MMMM 'de' yyyy",
      Locale.of("pt")
  );

  public static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
  public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  private DateFormatter() {
  }

  private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
      DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
      DateTimeFormatter.ISO_LOCAL_DATE_TIME
  );

  private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
      DateTimeFormatter.ofPattern("dd-MM-yyyy"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd"),
      DateTimeFormatter.ofPattern("yyyy/MM/dd"),
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ISO_LOCAL_DATE
  );

  public static LocalDateTime stringToLocalDateTime(String dateTimeStr) {
    if (dateTimeStr == null)
      return null;

    // Try parsing as LocalDateTime
    var parsedDateTime = DATE_TIME_FORMATTERS.stream()
        .map(formatter -> parseLocalDateTime(dateTimeStr, formatter))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();

    if (parsedDateTime.isPresent())
      return parsedDateTime.get();

    // If LocalDateTime parsing fails, try parsing as LocalDate and set time to midnight
    var parsedDate = getParsedDate(dateTimeStr);

    return parsedDate.map(LocalDate::atStartOfDay).orElse(null);
  }

  public static LocalDate stringToLocalDate(String dateStr) {
    return Optional.ofNullable(dateStr)
        .flatMap(DateFormatter::getParsedDate)
        .orElse(null);
  }

  private static @NotNull Optional<LocalDate> getParsedDate(String str) {
    return DATE_FORMATTERS.stream()
        .map(formatter -> parseLocalDate(str, formatter))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst();
  }

  public static String localDateTimeToString(LocalDateTime dateTime) {
    return Optional.ofNullable(dateTime)
        .map(DATE_TIME_FORMATTERS.getFirst()::format)
        .orElse("");
  }

  public static String localDateToString(LocalDate date) {
    return Optional.ofNullable(date)
        .map(DATE_FORMATTERS.getFirst()::format)
        .orElse("");
  }

  public static String localDateTimeToLocalDateString(LocalDateTime dateTime) {
    return Optional.ofNullable(dateTime)
        .map(localDateTime -> localDateTime.toLocalDate().format(DATE_FORMATTERS.getFirst()))
        .orElse("");
  }

  private static Optional<LocalDateTime> parseLocalDateTime(String dateTimeStr, DateTimeFormatter formatter) {
    try {
      return Optional.of(LocalDateTime.parse(dateTimeStr, formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  private static Optional<LocalDate> parseLocalDate(String dateStr, DateTimeFormatter formatter) {
    try {
      return Optional.of(LocalDate.parse(dateStr, formatter));
    } catch (DateTimeParseException e) {
      return Optional.empty();
    }
  }

  public static long monthsBetween(LocalDate start, LocalDate end) {
    return ChronoUnit.MONTHS.between(start, end);
  }
}
