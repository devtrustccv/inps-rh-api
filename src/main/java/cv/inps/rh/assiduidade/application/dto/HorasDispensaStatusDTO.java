package cv.inps.rh.assiduidade.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Saldo de horas de dispensa de um colaborador num mês.
 *
 * <p>As horas vêm sempre normalizadas em {@code HH:MM}, independentemente de
 * {@code RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA} estar gravado como número de horas
 * ("4") ou como {@code HH:MM} ("04:00") — os dois formatos coexistem em BD.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorasDispensaStatusDTO {

    /** Direito mensal, {@code HH:MM}. */
    private String horasDisponiveis;

    /** Já consumidas no mês, {@code HH:MM}. */
    private String horasUsadas;

    /** Por consumir no mês, {@code HH:MM}. Nunca negativo. */
    private String horasRestantes;

    private Integer horasDisponiveisMinutos;

    private Integer horasUsadasMinutos;

    private Integer horasRestantesMinutos;
}
