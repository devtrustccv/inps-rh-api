package cv.inps.rh.shared.application.detalhe;

import jakarta.persistence.metamodel.SingularAttribute;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Suporte comum aos testes do detalhe de alterações.
 *
 * <p>Em testes unitários o {@code EntityManagerFactory} não arranca, pelo que os atributos do metamodel
 * gerado ({@code XEntity_.campo}) ficam a {@code null}. Os {@link SingularAttribute} são por isso
 * simulados, apontando para os campos reais de {@link Pessoa} — é exactamente o que o Hibernate faz em
 * runtime.
 */
final class CamposDeTeste {

  private CamposDeTeste() {
  }

  /** Entidade de teste: um campo de cada tipo que o {@link Campos} sabe declarar. */
  static final class Pessoa {
    String nome;
    LocalDate dataInicio;
    BigDecimal salario;
    Direcao direcao;

    Pessoa(String nome, LocalDate dataInicio, BigDecimal salario, Direcao direcao) {
      this.nome = nome;
      this.dataInicio = dataInicio;
      this.salario = salario;
      this.direcao = direcao;
    }
  }

  /**
   * Faz de FK/proxy. {@link #getNome()} rebenta de propósito: é o equivalente a um proxy lazy não
   * inicializado com a sessão fechada. O motor nunca o pode chamar.
   */
  static final class Direcao {
    final Long id;

    Direcao(Long id) {
      this.id = id;
    }

    String getNome() {
      throw new IllegalStateException("LazyInitializationException simulada: o proxy foi inicializado");
    }
  }

  static final String TABELA = "RH_T_PESSOA";

  /** nome(1) · direcao(2, FK) · salario(3) · dataInicio(4). */
  static Campos<Pessoa> campos() {
    return Campos.de(Pessoa.class)
        .campo(atributo("nome", String.class), "Nome")
        .referencia(atributo("direcao", Direcao.class), "Direcção")
        .montante(atributo("salario", BigDecimal.class), "Salário")
        .data(atributo("dataInicio", LocalDate.class), "Data início");
  }

  @SuppressWarnings("unchecked")
  static <V> SingularAttribute<Pessoa, V> atributo(String nome, Class<V> tipo) {
    // Lenient: nem todos os testes usam os três métodos (getJavaType só interessa às FKs).
    SingularAttribute<Pessoa, V> attr = mock(SingularAttribute.class, withSettings().strictness(Strictness.LENIENT));
    try {
      doReturn(nome).when(attr).getName();
      doReturn(tipo).when(attr).getJavaType();
      doReturn(Pessoa.class.getDeclaredField(nome)).when(attr).getJavaMember();
    } catch (NoSuchFieldException e) {
      throw new IllegalArgumentException(nome, e);
    }
    return attr;
  }
}
