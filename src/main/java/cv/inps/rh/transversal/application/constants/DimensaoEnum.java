package cv.inps.rh.transversal.application.constants;

public enum DimensaoEnum {
    DIRECAO,
    SECCAO,
    CARGO,
    GENERO,
    LOCAL_TRABALHO,
    CARREIRA,
    ESCALAO,
    CATEGORIA,
    VINCULO,
    SITUACAO_LABORAL,
    MOBILIDADE,
    GRAU_ESCOLARIDADE,
    IDADE,
    ANTIGUIDADE,
    FAIXA_ETARIA,
    ESTRUTURA_REMUNERATORIA;

    public static boolean exists(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            DimensaoEnum.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
