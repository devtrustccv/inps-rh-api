package cv.inps.rh.shared.application.detalhe;

import cv.inps.rh.shared.application.detalhe.CamposDeTeste.Direcao;
import cv.inps.rh.shared.application.detalhe.CamposDeTeste.Pessoa;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CamposTest {

  private final List<Campos.Campo> campos = CamposDeTeste.campos().lista();

  @Test
  void ordemEhADeDeclaracao() {
    assertEquals(List.of("nome", "direcao", "salario", "dataInicio"),
        campos.stream().map(Campos.Campo::nome).toList());
  }

  @Test
  void rotuloAndaColadoAoCampo() {
    assertEquals(List.of("Nome", "Direcção", "Salário", "Data início"),
        campos.stream().map(Campos.Campo::rotulo).toList());
  }

  @Test
  void soAsReferenciasTemTipoDeFk() {
    assertFalse(campos.get(0).referencia());
    assertTrue(campos.get(1).referencia());
    assertEquals(Direcao.class, campos.get(1).tipoFk());
    assertNull(campos.get(2).tipoFk());
  }

  @Test
  void dataFormataDdMmYyyy() {
    assertEquals("01-10-2026", formatado(campos.get(3), pessoa()));
  }

  @Test
  void montanteSemZerosNemNotacaoCientifica() {
    var p = pessoa();
    p.salario = new BigDecimal("190336.00");
    assertEquals("190336", formatado(campos.get(2), p));

    p.salario = new BigDecimal("1E+3");
    assertEquals("1000", formatado(campos.get(2), p));
  }

  @Test
  void acessorDevolveNuloQuandoOCampoEstaVazio() {
    var p = pessoa();
    p.nome = null;
    assertNull(campos.get(0).acessor().apply(p));
  }

  @Test
  void acessorDaFkDevolveOObjectoSemLheTocar() {
    // A Direcao rebenta se alguém lhe chamar getNome(): é um proxy lazy de sessão fechada.
    var p = pessoa();
    assertSame(p.direcao, campos.get(1).acessor().apply(p));
  }

  @Test
  void listaNaoSePodeAlterarPorFora() {
    var lista = CamposDeTeste.campos().lista();
    assertThrows(UnsupportedOperationException.class, () -> lista.remove(0));
  }

  /** O que o serviço grava para um campo escalar: o formatador aplicado ao valor lido. */
  private static String formatado(Campos.Campo campo, Pessoa p) {
    return campo.formatador().apply(campo.acessor().apply(p));
  }

  private static Pessoa pessoa() {
    return new Pessoa("Ana", LocalDate.of(2026, 10, 1), new BigDecimal("1000"), new Direcao(12L));
  }
}
