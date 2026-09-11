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

    /** Já consumidas no mês por dispensas aprovadas, {@code HH:MM}. */
    private String horasUsadas;

    /**
     * Comprometidas por faltas ainda pendentes de despacho que tencionam deduzir em
     * dispensa, {@code HH:MM}. Ainda não foram consumidas — se o pedido for rejeitado
     * voltam sozinhas ao saldo — mas já não estão disponíveis para um pedido novo.
     *
     * <p>Contam para {@link #horasRestantes} e são deliberadamente mantidas fora de
     * {@link #horasUsadas}: somadas ali, o ecrã mostrava "32:00 usadas" de um direito de
     * 4 horas, um número que ninguém consegue explicar ao utilizador.
     */
    private String horasReservadas;

    /** Por consumir no mês, {@code HH:MM}. Já líquido das reservadas. Nunca negativo. */
    private String horasRestantes;

    private Integer horasDisponiveisMinutos;

    private Integer horasUsadasMinutos;

    private Integer horasReservadasMinutos;

    private Integer horasRestantesMinutos;
}
