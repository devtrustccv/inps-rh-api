package cv.inps.rh.shared.util;

import org.springframework.util.StringUtils;

public class TimeUtils {

  public static String formatMinutes(Integer minutes) {
    if (minutes == null) {
      return null;
    }
    int h = minutes / 60;
    int m = minutes % 60;

    return String.format("%02d:%02d", h, m);
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
      return null; // ou "+0 00:00:00" se preferir default
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


  public static Integer diffMinutes(String inicio, String fim) {
    if (!StringUtils.hasText(inicio) || !StringUtils.hasText(fim)) {
      return null;
    }

    try {
      var t1 = java.time.LocalTime.parse(inicio);
      var t2 = java.time.LocalTime.parse(fim);
      long minutes = java.time.Duration.between(t1, t2).toMinutes();
      return (int) Math.max(minutes, 0);
    } catch (Exception e) {
      return null;
    }
  }


}
