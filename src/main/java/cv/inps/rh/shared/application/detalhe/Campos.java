package cv.inps.rh.shared.application.detalhe;

import jakarta.persistence.metamodel.SingularAttribute;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Declaração <b>tipada</b> dos campos que a grelha "Detalhe de alterações" mostra para uma entidade,
 * assente no metamodel JPA ({@code hibernate-jpamodelgen}, já no pom e já usado nas Specifications do
 * módulo {@code configuracao}).
 *
 * <p>Cada {@link SingularAttribute} traz <b>nome + tipo</b> do campo:
 * <ul>
 *   <li>o <b>nome</b> alimenta a coluna {@code CAMPO} sem strings mágicas — renomear o campo na
 *       entidade regenera o metamodel e parte a compilação, em vez de esvaziar a grelha em silêncio;</li>
 *   <li>o <b>tipo</b> impede declarar uma data como texto e, nas FKs, dá a classe da entidade
 *       referenciada sem ser preciso passar lambda nenhuma.</li>
 * </ul>
 *
 * <p>A {@code ORDEM} da grelha é a ordem de declaração.
 *
 * <p><b>As FKs nunca são inicializadas aqui.</b> O acessor devolve o proxy tal como está; quem lhe
 * extrai o id é o {@link DetalheAlteracoesService}, via {@code PersistenceUnitUtil} — contrato JPA
 * que garante não haver SELECT. O nome legível resolve-se depois, por PK-lookup. Ler {@code getNome()}
 * no proxy seria o caminho directo para {@code LazyInitializationException} com
 * {@code open-in-view=false}.
 *
 * <p><b>Armadilha:</b> os {@code SingularAttribute} são {@code volatile} e só ficam preenchidos
 * quando o {@code EntityManagerFactory} arranca. Declarar isto num campo {@code static} (ou num
 * {@code @PostConstruct} precoce) dá {@code NullPointerException} — usar sempre {@code Lazy}, como
 * em {@code MobilidadeCampos}.
 */
public final class Campos<T> {

  private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  /**
   * Um campo da grelha, já com o acessor e o formatador resolvidos.
   *
   * @param tipoFk classe da entidade referenciada, ou {@code null} se o campo for escalar
   */
  public record Campo(String nome, String rotulo, Class<?> tipoFk,
                      Function<Object, Object> acessor, Function<Object, String> formatador) {

    public boolean referencia() {
      return tipoFk != null;
    }
  }

  private final List<Campo> campos = new ArrayList<>();

  private Campos() {
  }

  /** Abre a declaração. O tipo serve só para fixar {@code T} — nada é lido dele. */
  public static <T> Campos<T> de(Class<T> tipo) {
    return new Campos<>();
  }

  /** Escalar: texto, enum, número inteiro. Formatado com {@code toString()}. */
  public <V> Campos<T> campo(SingularAttribute<? super T, V> attr, String rotulo) {
    return juntar(attr, rotulo, null, Object::toString);
  }

  /** Data — apresentada dd-MM-yyyy, como no resto do dossiê. */
  public Campos<T> data(SingularAttribute<? super T, LocalDate> attr, String rotulo) {
    return juntar(attr, rotulo, null, v -> ((LocalDate) v).format(DATA));
  }

  /** Montante — sem zeros à direita nem notação científica. */
  public Campos<T> montante(SingularAttribute<? super T, BigDecimal> attr, String rotulo) {
    return juntar(attr, rotulo, null, v -> ((BigDecimal) v).stripTrailingZeros().toPlainString());
  }

  /**
   * FK. Sem lambda: a classe da entidade referenciada vem do próprio metamodel
   * ({@code SingularAttribute<MobilidadeEntity, DirecaoEntity>} → {@code DirecaoEntity.class}).
   */
  public <V> Campos<T> referencia(SingularAttribute<? super T, V> attr, String rotulo) {
    return juntar(attr, rotulo, attr.getJavaType(), Object::toString);
  }

  /** Os campos declarados, pela ordem em que foram declarados. */
  public List<Campo> lista() {
    return List.copyOf(campos);
  }

  private Campos<T> juntar(SingularAttribute<? super T, ?> attr, String rotulo, Class<?> tipoFk,
      Function<Object, String> formatador) {
    campos.add(new Campo(attr.getName(), rotulo, tipoFk, acessor(attr), formatador));
    return this;
  }

  /**
   * Lê o campo directamente pelo membro Java que o metamodel aponta. Este projeto usa field access
   * (as anotações JPA estão nos campos), por isso cai quase sempre no ramo do {@link Field} — que é o
   * que queremos: devolve a FK como está (proxy incluído), sem a inicializar.
   */
  private static Function<Object, Object> acessor(SingularAttribute<?, ?> attr) {
    Member membro = attr.getJavaMember();
    if (membro instanceof Field f) {
      f.setAccessible(true);
      return alvo -> {
        try {
          return f.get(alvo);
        } catch (IllegalAccessException e) {
          throw new IllegalStateException("Sem acesso ao campo " + f.getName(), e);
        }
      };
    }
    if (membro instanceof Method m) {
      m.setAccessible(true);
      return alvo -> {
        try {
          return m.invoke(alvo);
        } catch (ReflectiveOperationException e) {
          throw new IllegalStateException("Sem acesso ao getter " + m.getName(), e);
        }
      };
    }
    throw new IllegalStateException("Atributo sem membro Java: " + attr.getName());
  }
}
