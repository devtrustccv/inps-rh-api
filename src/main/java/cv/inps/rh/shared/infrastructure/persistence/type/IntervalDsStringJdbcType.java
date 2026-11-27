package cv.inps.rh.shared.infrastructure.persistence.type;

import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

/**
 * Campo {@code String} numa coluna Oracle {@code INTERVAL DAY TO SECOND}.
 *
 * <p>Lê e grava como texto, igual a um VARCHAR: o Java grava {@code "+0 08:30:00"} e o Oracle converte; na
 * leitura o driver devolve {@code "0 8:0:0.0"} (ver {@code TimeUtils}). Só muda o tipo declarado ao Hibernate,
 * para o {@code ddl-auto=validate} reconhecer a coluna como INTERVAL em vez de exigir VARCHAR2.
 *
 * <p>Uso, sempre com o {@code columnDefinition} da coluna real:
 * <pre>{@code
 * @JdbcType(IntervalDsStringJdbcType.class)
 * @Column(name = "horas_ausencia", columnDefinition = IntervalDsStringJdbcType.DDL)
 * private String horasAusencia;
 * }</pre>
 */
public class IntervalDsStringJdbcType extends VarcharJdbcType {

  /** {@code oracle.jdbc.OracleTypes.INTERVALDS}. */
  public static final int INTERVALDS = -104;

  /** Tipo das colunas de horas na BD. */
  public static final String DDL = "interval day(0) to second(0)";

  @Override
  public int getJdbcTypeCode() {
    return INTERVALDS;
  }

  @Override
  public int getDefaultSqlTypeCode() {
    return INTERVALDS;
  }
}
