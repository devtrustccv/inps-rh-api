
package cv.inps.rh.shared.application.enums;

import lombok.Getter;

@Getter
public enum SituacaoFalta {
    FERIAS("Férias");

    private final String nome;

    SituacaoFalta(String nome) {
        this.nome = nome;
    }
}
