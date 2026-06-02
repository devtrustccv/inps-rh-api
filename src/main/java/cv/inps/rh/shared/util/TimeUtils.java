package cv.inps.rh.shared.util;

import org.springframework.util.StringUtils;

public class TimeUtils {

  private TimeUtils() {
  }

  public static String formatMinutesToHHmm(Integer minutes) {
    if (minutes == null) {
      return "00:00";
    }
    int h = minutes / 60;
    int m = minutes % 60;

    return String.format("%02d:%02d", h, m);
  }

  public static int hhmmToMinutes(String hhmm) {
    if (!StringUtils.hasText(hhmm)) {
      return 0;
    }

    try {
      var time = java.time.LocalTime.parse(hhmm);
      return time.getHour() * 60 + time.getMinute();
    } catch (Exception _) {
      return 0;
    }
  }


  public static String intervalFormatToHHmm(String interval) {
    if (interval == null || interval.isBlank()) {
      return "00:00";
    }

    try {
      // Divide pelo espaço para pegar a parte do tempo
      String[] parts = interval.trim().split("\\s+");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Formato inválido de interval: " + interval);
      }

      // Pega HH:MM:SS
      String timePart = parts[1];
      String[] hm = timePart.split(":");

      if (hm.length < 2) {
        throw new IllegalArgumentException("Formato inválido de tempo: " + timePart);
      }

      int hours = Integer.parseInt(hm[0]);
      int minutes = Integer.parseInt(hm[1]);

      return String.format("%02d:%02d", hours, minutes);

    } catch (Exception e) {
      throw new IllegalArgumentException("Erro ao converter interval para HH:MM: " + interval, e);
    }
  }

  /**
   * Converte uma string "HH:MM" em formato aceito por Oracle INTERVAL DAY TO SECOND.
   * Retorna "+0 HH:MM:SS"
   */
  public static String hhmmToIntervalFormat(String hhmm) {
    if (!StringUtils.hasText(hhmm)) {
      return "+0 00:00:00";
    }

    String[] parts = hhmm.trim().split(":");
    if (parts.length != 2) {
      throw new IllegalArgumentException("Formato inválido de hora: " + hhmm);
    }

    int hours = Integer.parseInt(parts[0]);
    int minutes = Integer.parseInt(parts[1]);

    if (hours < 0 || hours > 23) {
      throw new IllegalArgumentException("Horas inválidas: " + hours);
    }
    if (minutes < 0 || minutes > 59) {
      throw new IllegalArgumentException("Minutos inválidos: " + minutes);
    }

    return String.format("+0 %02d:%02d:00", hours, minutes);
  }

  public static int diffMinutes(String inicio, String fim) {
    if (!StringUtils.hasText(inicio) || !StringUtils.hasText(fim)) {
      return 0;
    }
    try {
      return (int) Math.max(toMinutes(fim) - toMinutes(inicio), 0);
    } catch (Exception e) {
      return 0;
    }
  }

  private static long toMinutes(String time) {
    // suporta "+0 HH:MM:SS" (Oracle interval) e "HH:MM"
    String t = time.trim();
    if (t.startsWith("+")) {
      // "+0 HH:MM:SS" → extrair HH:MM
      int spaceIdx = t.indexOf(' ');
      t = spaceIdx >= 0 ? t.substring(spaceIdx + 1) : t.substring(1);
    }
    String[] parts = t.split(":");
    long hours = Long.parseLong(parts[0]);
    long minutes = parts.length > 1 ? Long.parseLong(parts[1]) : 0;
    return hours * 60 + minutes;
  }


}
