package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriadoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dias úteis: segunda a sexta, sem os feriados activos de RH_T_PARAM_FERIADO.
 *
 * <p>Decisão do negócio (23/11/2026): uma falta só conta nos dias úteis — fins-de-semana e feriados
 * dentro do período não geram falta, síntese diária nem desconto. O formulário (Marcar Falta) conta da
 * mesma forma (useDiasUteis / calculateWorkingDaysWithHolidays), para o Total Dias bater.</p>
 *
 * <p>Feriados: os "fixos" (FIXO_ANO = SIM) repetem-se todos os anos em DIA/MES; os outros valem na
 * DATA_ESPECIFICA. Como no ecrã, contam todos os feriados activos (incluindo os regionais).</p>
 */
@Service
@RequiredArgsConstructor
public class DiasUteisService {

  private static final String FIXO_ANO = "SIM";

  private final FeriadoEntityRepository feriadoEntityRepository;

  /** Os dias úteis de {@code inicio} a {@code fim}, inclusive, por ordem. */
  @Transactional(readOnly = true)
  public List<LocalDate> diasUteis(LocalDate inicio, LocalDate fim) {
    var feriados = feriadosEntre(inicio.getYear(), fim.getYear());
    var dias = new ArrayList<LocalDate>();
    for (var d = inicio; !d.isAfter(fim); d = d.plusDays(1)) {
      if (eDiaUtil(d, feriados)) dias.add(d);
    }
    return dias;
  }

  private static boolean eDiaUtil(LocalDate dia, Set<LocalDate> feriados) {
    var dow = dia.getDayOfWeek();
    return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY && !feriados.contains(dia);
  }

  private Set<LocalDate> feriadosEntre(int anoInicio, int anoFim) {
    var feriados = new HashSet<LocalDate>();
    for (int ano = anoInicio; ano <= anoFim; ano++) {
      for (var f : feriadoEntityRepository.findAllByAnoReferenteAndEstado(ano, Estado.A)) {
        if (FIXO_ANO.equalsIgnoreCase(f.getFixoAno()) && f.getDia() != null && f.getMes() != null) {
          try {
            feriados.add(LocalDate.of(ano, f.getMes(), f.getDia()));
          } catch (DateTimeException ignored) {
            // dia/mês inválidos na parametrização (ex.: 29/02 num ano comum) — não é feriado nesse ano
          }
        } else if (f.getDataEspecifica() != null) {
          feriados.add(f.getDataEspecifica());
        }
      }
    }
    return feriados;
  }
}
