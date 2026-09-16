package cv.inps.rh.shared.application.detalhe;

import cv.inps.rh.shared.application.detalhe.CamposDeTeste.Direcao;
import cv.inps.rh.shared.application.detalhe.CamposDeTeste.Pessoa;
import cv.inps.rh.shared.application.detalhe.DetalheAlteracoes.Estado;
import cv.inps.rh.shared.application.detalhe.DetalheAlteracoes.Valor;
import cv.inps.rh.shared.application.service.ReferenciaNomeResolver;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetalheAlteracoesServiceTest {

  private static final String TABELA = CamposDeTeste.TABELA;
  private static final UUID VALIDACAO_UUID = UUID.fromString("01a0ab11-1aa1-71a7-aada-1e7aaeba0cc8");

  @Mock private EntityManagerFactory entityManagerFactory;
  @Mock private PersistenceUnitUtil persistenceUnitUtil;
  @Mock private ReferenciaNomeResolver referenciaNomeResolver;
  @Mock private ValidacaoDetalheEntityRepository detalheRepo;
  @Mock private ValidacaoEntityRepository validacaoRepo;
  @Mock private EntityManager entityManager;

  @Captor private ArgumentCaptor<List<ValidacaoDetalheEntity>> gravadasCaptor;

  @InjectMocks private DetalheAlteracoesService service;

  private final Campos<Pessoa> campos = CamposDeTeste.campos();

  @BeforeEach
  void setUp() {
    // @PersistenceContext não é injectado por construtor.
    ReflectionTestUtils.setField(service, "entityManager", entityManager);

    // O id da FK vem do PersistenceUnitUtil (nunca do proxy); o nome vem do resolver, por PK.
    lenient().when(entityManagerFactory.getPersistenceUnitUtil()).thenReturn(persistenceUnitUtil);
    lenient().when(persistenceUnitUtil.getIdentifier(any())).thenAnswer(i -> ((Direcao) i.getArgument(0)).id);
    lenient().when(referenciaNomeResolver.resolver(eq(Direcao.class), any()))
        .thenAnswer(i -> "Direcao " + i.getArgument(1));
  }

  // ==================================================================================================

  @Nested
  class Capturar {

    @Test
    void criacaoComAlvoNuloDaTudoVazio() {
      Estado estado = service.capturar(campos, null);

      assertEquals(4, estado.valores().size());
      assertTrue(estado.valores().stream().allMatch(Valor::vazio));
      verifyNoInteractions(referenciaNomeResolver);
    }

    @Test
    void fkLeIdSemInicializarOProxyEResolveNomePorPk() {
      // Direcao.getNome() rebenta: se o motor tocasse no proxy, este teste falhava.
      Estado estado = service.capturar(campos, pessoa("Ana", 12L));

      Valor direcao = estado.valores().get(1);
      assertEquals(12L, direcao.id());
      assertEquals("Direcao 12", direcao.display());
      verify(referenciaNomeResolver).resolver(Direcao.class, 12L);
    }

    @Test
    void fkNulaNaoChamaOResolver() {
      Estado estado = service.capturar(campos, pessoa("Ana", null));

      assertTrue(estado.valores().get(1).vazio());
      verifyNoInteractions(referenciaNomeResolver);
    }

    @Test
    void escalaresGuardamODisplaySemId() {
      Valor nome = service.capturar(campos, pessoa("Ana", 12L)).valores().get(0);

      assertEquals("Ana", nome.display());
      assertNull(nome.id());
    }
  }

  // ==================================================================================================

  @Nested
  class Comparar {

    @Test
    void soDevolveOQueMudouPelaOrdemDeDeclaracao() {
      var antes = pessoa("Ana", 12L);
      var depois = pessoa("Ana", 12L);
      depois.dataInicio = LocalDate.of(2026, 11, 1);   // 4.º declarado
      depois.nome = "Ana Maria";                        // 1.º declarado

      var alterados = service.comparar(campos, service.capturar(campos, antes), service.capturar(campos, depois));

      assertEquals(List.of("nome", "dataInicio"), alterados.stream().map(CampoAlterado::campo).toList());
      assertEquals(List.of(1, 4), alterados.stream().map(CampoAlterado::ordem).toList());
    }

    @Test
    void semAlteracoesDevolveListaVazia() {
      var estado = service.capturar(campos, pessoa("Ana", 12L));

      assertTrue(service.comparar(campos, estado, estado).isEmpty());
    }

    @Test
    void fkComparaPorIdNaoPorNome() {
      // A direcção 12 foi renomeada entre as duas capturas: não é alteração do registo.
      var antes = estado(new Valor("Direcção Antiga", 12L));
      var depois = estado(new Valor("Direcção Nova", 12L));

      assertTrue(service.comparar(campos, antes, depois).isEmpty());
    }

    @Test
    void fkComIdDiferenteEhReferenciaComOsDoisIds() {
      var alterados = service.comparar(campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("Ana", 18L)));

      var direcao = alterados.get(0);
      assertEquals(CampoAlterado.Tipo.REFERENCIA, direcao.tipo());
      assertEquals("Direcao 12", direcao.valorAnterior());
      assertEquals(12L, direcao.valorAnteriorId());
      assertEquals("Direcao 18", direcao.valorNovo());
      assertEquals(18L, direcao.valorNovoId());
      assertEquals("Direcção", direcao.rotulo());
    }

    @Test
    void valorAnteriorVazioEhInicial() {
      var alterados = service.comparar(campos,
          service.capturar(campos, null),
          service.capturar(campos, pessoa("Ana", 12L)));

      assertEquals(4, alterados.size());
      assertTrue(alterados.stream().allMatch(c -> c.tipo() == CampoAlterado.Tipo.INICIAL));
    }

    @Test
    void escalarAlteradoEhValor() {
      var depois = pessoa("Ana", 12L);
      depois.salario = new BigDecimal("2000");

      var alterados = service.comparar(campos,
          service.capturar(campos, pessoa("Ana", 12L)), service.capturar(campos, depois));

      assertEquals(CampoAlterado.Tipo.VALOR, alterados.get(0).tipo());
      assertEquals("1000", alterados.get(0).valorAnterior());
      assertEquals("2000", alterados.get(0).valorNovo());
    }

    @Test
    void estadoCapturadoComOutraDeclaracaoRebenta() {
      var curto = new Estado(List.of(Valor.VAZIO));
      var completo = service.capturar(campos, null);

      assertThrows(IllegalArgumentException.class, () -> service.comparar(campos, curto, completo));
    }
  }

  // ==================================================================================================

  @Nested
  class Congelar {

    @Test
    void gravaUmaLinhaPorAlteracaoComTodasAsColunas() {
      var validacao = validacao(10L);
      semDetalheCongelado();

      int n = service.congelar(validacao, TABELA, campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("Ana", 18L)));

      assertEquals(1, n);
      var linha = gravadas().get(0);
      assertSame(validacao, linha.getValidacaoId());
      assertEquals("direcao", linha.getCampo());
      assertEquals("Direcção", linha.getCampoAlterado());   // o rótulo continua a ir para CAMPO_ALTERADO
      assertEquals(2, linha.getOrdem());
      assertEquals("REFERENCIA", linha.getTipoAlteracao());
      assertEquals("Direcao 12", linha.getValorAnterior());
      assertEquals(12L, linha.getValorAnteriorId());
      assertEquals("Direcao 18", linha.getValorNovo());
      assertEquals(18L, linha.getValorNovoId());
      assertEquals(TABELA, linha.getTabelaName());
      assertNull(linha.getTabelaId());
      assertNotNull(linha.getUuid());
    }

    @Test
    void semAlteracoesNaoGravaLinhas() {
      semDetalheCongelado();
      var estado = service.capturar(campos, pessoa("Ana", 12L));

      assertEquals(0, service.congelar(validacao(10L), TABELA, campos, estado, estado));
      assertTrue(gravadas().isEmpty());
    }

    @Test
    void colecaoLevaOIdDaLinhaParaTabelaId() {
      semDetalheCongelado();

      service.congelar(validacao(10L), TABELA, 699L, campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("Bia", 12L)));

      assertEquals(699L, gravadas().get(0).getTabelaId());
    }

    @Test
    void valorMaiorQueAColunaEhTruncadoA2000() {
      semDetalheCongelado();

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("x".repeat(2500), 12L)));

      var novo = gravadas().get(0).getValorNovo();
      assertEquals(2000, novo.length());
      assertTrue(novo.endsWith("…"));
    }
  }

  // ==================================================================================================

  /**
   * Reenvio de correcção: os registos são editados in place, pelo que o "antes" de uma correcção é o
   * estado PENDENTE que o checker devolveu, não o último aprovado. A fusão com o que já estava
   * congelado repõe a verdade.
   */
  @Nested
  class FusaoNoReenvio {

    @Test
    void mantemOValorAnteriorAprovadoEAdoptaONovo() {
      // 1.ª submissão: DARH(12) -> DGFI(4). Checker devolve. Correcção: DGFI(4) -> DCC(6).
      var congelada = linha("direcao", "Direcção", "Direcao 12", 12L, "Direcao 4", 4L, 2, "REFERENCIA", null);
      jaCongelado(congelada);

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pessoa("Ana", 4L)),
          service.capturar(campos, pessoa("Ana", 6L)));

      var direcao = gravadas().get(0);
      assertEquals(12L, direcao.getValorAnteriorId());   // o aprovado, não o 4 que foi rejeitado
      assertEquals(6L, direcao.getValorNovoId());
      verify(detalheRepo).delete(congelada);
    }

    @Test
    void campoAlteradoSoNaPrimeiraSubmissaoMantemSe() {
      jaCongelado(linha("salario", "Salário", "1000", null, "2000", null, 3, "VALOR", null));
      var pendente = pessoa("Ana", 12L);
      pendente.salario = new BigDecimal("2000");
      var corrigida = pessoa("Bia", 12L);
      corrigida.salario = new BigDecimal("2000");   // a correcção não voltou a tocar no salário

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pendente), service.capturar(campos, corrigida));

      assertEquals(List.of("nome", "salario"), gravadas().stream().map(ValidacaoDetalheEntity::getCampo).toList());
      assertEquals("1000", gravadas().get(1).getValorAnterior());
      assertEquals("2000", gravadas().get(1).getValorNovo());
    }

    @Test
    void makerQueRevertiuNaoDeixaLinha() {
      jaCongelado(linha("nome", "Nome", "Ana", null, "Bia", null, 1, "VALOR", null));

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pessoa("Bia", 12L)),
          service.capturar(campos, pessoa("Ana", 12L)));

      assertTrue(gravadas().isEmpty());
    }

    @Test
    void reversaoDeFkDetectaSePorId() {
      jaCongelado(linha("direcao", "Direcção", "Direcao 12", 12L, "Direcao 18", 18L, 2, "REFERENCIA", null));

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pessoa("Ana", 18L)),
          service.capturar(campos, pessoa("Ana", 12L)));

      assertTrue(gravadas().isEmpty());
    }

    @Test
    void registoNovoContinuaInicialDepoisDaCorreccao() {
      jaCongelado(linha("nome", "Nome", null, null, "Ana", null, 1, "INICIAL", null));

      service.congelar(validacao(10L), TABELA, campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("Bia", 12L)));

      var nome = gravadas().get(0);
      assertNull(nome.getValorAnterior());
      assertEquals("Bia", nome.getValorNovo());
      assertEquals("INICIAL", nome.getTipoAlteracao());
    }

    @Test
    void naoTocaEmLinhasDeOutraTabelaNemDeOutroRegisto() {
      var outraTabela = linha("nome", "Nome", "X", null, "Y", null, 1, "VALOR", null);
      outraTabela.setTabelaName("RH_T_OUTRA");
      var outroRegisto = linha("nome", "Nome", "X", null, "Y", null, 1, "VALOR", 700L);
      var desteRegisto = linha("nome", "Nome", "Ana", null, "Bia", null, 1, "VALOR", 699L);
      jaCongelado(outraTabela, outroRegisto, desteRegisto);

      service.congelar(validacao(10L), TABELA, 699L, campos,
          service.capturar(campos, pessoa("Bia", 12L)),
          service.capturar(campos, pessoa("Carla", 12L)));

      verify(detalheRepo).delete(desteRegisto);
      verify(detalheRepo, never()).delete(outraTabela);
      verify(detalheRepo, never()).delete(outroRegisto);
      assertEquals("Ana", gravadas().get(0).getValorAnterior());
    }
  }

  // ==================================================================================================

  /**
   * Alguns módulos criam a validação acrescentando-a a {@code funcionario.getValidacoes()} e gravando o
   * funcionário. {@code save()} faz MERGE: quem fica gerido é uma cópia e a instância do chamador
   * continua sem id.
   */
  @Nested
  class ValidacaoTransiente {

    @Test
    void recuperaAInstanciaGeridaPeloUuidENuncaFazPersist() {
      var transiente = validacao(null);
      var gerida = validacao(10L);
      when(validacaoRepo.findByUuid(VALIDACAO_UUID)).thenReturn(Optional.of(gerida));
      semDetalheCongelado();

      service.congelar(transiente, TABELA, campos,
          service.capturar(campos, pessoa("Ana", 12L)),
          service.capturar(campos, pessoa("Bia", 12L)));

      verify(entityManager).flush();
      verify(entityManager, never()).persist(any());   // persist criava uma validação duplicada
      assertSame(gerida, gravadas().get(0).getValidacaoId());
    }

    @Test
    void semInstanciaGeridaFalhaAlto() {
      when(validacaoRepo.findByUuid(VALIDACAO_UUID)).thenReturn(Optional.empty());
      var estado = service.capturar(campos, pessoa("Ana", 12L));

      assertThrows(IllegalStateException.class,
          () -> service.congelar(validacao(null), TABELA, campos, estado, estado));
      verify(detalheRepo, never()).saveAll(any());
    }

    @Test
    void validacaoJaPersistidaNaoFazFlush() {
      semDetalheCongelado();
      var estado = service.capturar(campos, pessoa("Ana", 12L));

      service.congelar(validacao(10L), TABELA, campos, estado, estado);

      verifyNoInteractions(entityManager, validacaoRepo);
    }
  }

  // ==================================================================================================

  private static Pessoa pessoa(String nome, Long direcaoId) {
    return new Pessoa(nome, LocalDate.of(2026, 10, 1), new BigDecimal("1000"),
        direcaoId == null ? null : new Direcao(direcaoId));
  }

  private static ValidacaoEntity validacao(Long id) {
    var v = new ValidacaoEntity();
    v.setId(id);
    v.setUuid(VALIDACAO_UUID);
    return v;
  }

  /** Estado com a direcção dada e os restantes campos vazios. */
  private static Estado estado(Valor direcao) {
    return new Estado(List.of(Valor.VAZIO, direcao, Valor.VAZIO, Valor.VAZIO));
  }

  private static ValidacaoDetalheEntity linha(String campo, String rotulo, String antes, Long antesId,
      String novo, Long novoId, int ordem, String tipo, Long tabelaId) {
    var e = new ValidacaoDetalheEntity();
    e.setCampo(campo);
    e.setCampoAlterado(rotulo);
    e.setValorAnterior(antes);
    e.setValorAnteriorId(antesId);
    e.setValorNovo(novo);
    e.setValorNovoId(novoId);
    e.setOrdem(ordem);
    e.setTipoAlteracao(tipo);
    e.setTabelaName(TABELA);
    e.setTabelaId(tabelaId);
    return e;
  }

  private void semDetalheCongelado() {
    jaCongelado();
  }

  private void jaCongelado(ValidacaoDetalheEntity... linhas) {
    when(detalheRepo.findByValidacaoId_UuidOrderByTabelaNameAscIdAsc(VALIDACAO_UUID))
        .thenReturn(new ArrayList<>(List.of(linhas)));
  }

  private List<ValidacaoDetalheEntity> gravadas() {
    verify(detalheRepo).saveAll(gravadasCaptor.capture());
    return gravadasCaptor.getValue();
  }
}
