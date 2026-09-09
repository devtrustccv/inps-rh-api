# Proposta — "Detalhe de alterações" sem auditoria

**Data:** 2026-09-03
**Estado:** proposta, por aprovar
**Substitui:** a grelha servida pelo histórico do JaVers (`JaversValidacaoDetalheReadService`)

---

## 1. O problema, dito correctamente

A grelha "Detalhe de alterações" existe para uma coisa: **o aprovador ver o que muda e decidir**.

Isso não é auditoria. Auditoria é forense — olha para trás, tem de apanhar tudo, tem retenção
legal e ninguém a lê no dia-a-dia. O que temos é um problema de **comparação de dois estados no
momento de uma decisão**: sem histórico, sem retenção, sem "quem mudou o quê em Março".

O sistema actual resolve o segundo problema com a ferramenta do primeiro. O JaVers está montado
como *store de auditoria* — tabelas `JV_*`, commits, propriedades de commit, aspectos AOP,
`ThreadLocal` — para responder a uma pergunta que não envolve armazenamento nenhum.

### Sintomas concretos

| Sintoma | Causa |
|---|---|
| `ValidacaoAuditContext.set()` em 16 write-services; esquecer = **grelha vazia sem erro** | o `CommitPropertiesProvider` do JaVers é um bean global sem argumentos |
| Alteração de escalão servida por fora (`AlteracaoEscalaoDetalheReadService`) | o tiprel é *Shallow Reference* → grava `STATE={}`, os campos nunca aparecem |
| POJOs `FuncionarioDadosPessoaisSnapshot` / `ContratoDadosSnapshot` | criados só para dar ao JaVers algo que fotografar, por Funcionário/Contrato serem rasos |
| *Shallow Reference* obrigatório | sem ele, um commit percorria o grafo e demorava **20–50 s** |
| Fiação manual do JaVers (dialecto, schema-management) | `OracleLegacyDialect` não reconhecido; inspector do polyjdbc vs maiúsculas do Oracle |

~1500 linhas de código à volta de uma comparação de dois objectos.

---

## 2. Decisão

**Modelo (a) — derivar na leitura.**

O estado pendente **já existe como linha** (`estado = 'P'`). O "antes" é a linha `A`, o "depois"
é a linha `P`. O detalhe calcula-se quando o aprovador abre o ecrã.

Isto não é teórico: é o que o `AlteracaoEscalaoDetalheReadService` já faz — compara o tiprel
pendente com o predecessor e produz antes→depois reais. Foi escrito como *excepção* ao JaVers e é
a peça que funciona melhor. **Generaliza-se a excepção e passa a ser a regra.**

### Duas fases

| Fase | Onde vive o detalhe |
|---|---|
| **Pendente** | em lado nenhum — calculado a cada pedido |
| **Decidido** (aprovado/rejeitado) | congelado em `RH_T_VALIDACAO_DETALHE`, escrito uma vez |

Enquanto pendente, calcular ao vivo é **sempre verdade**: se alguém mexer na linha `P`, a grelha
reflecte-o na hora. Uma tabela escrita no registo ficaria desactualizada em silêncio.

Congelar na decisão fecha o único furo do modelo: depois de consolidar, o "antes" desaparece e o
diff deixaria de ser recalculável. Dá também valor de prova — *isto foi o que foi apresentado ao
aprovador*.

> **Ordem crítica:** calcular e gravar o detalhe **ANTES** de consolidar. Invertido, o "antes" já
> foi por cima e a grelha grava linhas vazias.

### Sem JSON

O payload JSON só teria função se o estado proposto não existisse em lado nenhum. Aqui a linha `P`
**é** o payload. Um JSON seria uma segunda cópia da mesma coisa, com o dobro dos sítios para
dessincronizar. Tudo relacional: sem CLOBs, sem `JSON_VALUE`, sem *schema drift*.

---

## 3. O que fica do JaVers

Só o motor de diff em memória — a metade *core*:

```java
Javers javers = JaversBuilder.javers().build();   // sem repositório, sem SQL
Diff diff = javers.compare(antes, depois);
```

Função pura sobre dois objectos. Não cria tabelas, não faz commits, não precisa de
`AuthorProvider`, `CommitPropertiesProvider`, `ThreadLocal`, aspectos AOP nem
`@JaversSpringDataAuditable`.

| Metade | Serve para | Traz | Decisão |
|---|---|---|---|
| `javers.compare(a, b)` | comparar dois estados **agora** | nada — função pura | **fica** |
| `javers.commit()` + repositório | histórico ao longo do **tempo** | tabelas, aspectos, contexto | **sai** |

**Auditoria a sério** (se e quando for pedida) fica para o **Hibernate Envers**, num eixo
independente: `@Audited` na entidade, tabelas `X_AUD` + `REVINFO`, engancha nos eventos de flush
(apanha *dirty checking*, coisa que o arranjo actual não apanha). Limite honesto e comum às duas
ferramentas: **procedures e SQL nativo continuam invisíveis** — se a auditoria tiver de ser
completa para conformidade, a peça é na base (Flashback Data Archive ou triggers).

---

## 4. Esquema — `RH_T_VALIDACAO_DETALHE`

```sql
CREATE TABLE RH_T_VALIDACAO_DETALHE (
  ID              NUMBER          NOT NULL,
  VALIDACAO_ID    NUMBER          NOT NULL,
  UUID            VARCHAR2(36),

  ORDEM           NUMBER(3)       NOT NULL,   -- ordem de apresentação na grelha
  CAMPO           VARCHAR2(100)   NOT NULL,   -- nome técnico: dirId, dataInicio
  CAMPO_LABEL     VARCHAR2(150)   NOT NULL,   -- rótulo mostrado: "Direção"
  TIPO_ALTERACAO  VARCHAR2(20)    NOT NULL,   -- VALOR | REFERENCIA | INICIAL

  VALOR_ANTERIOR  VARCHAR2(2000),
  VALOR_NOVO      VARCHAR2(2000),

  TABELA_NAME     VARCHAR2(50),
  TABELA_ID       NUMBER,

  ALTERADO_POR    VARCHAR2(150)   NOT NULL,   -- congelado: o MAKER
  DATA_ALTERACAO  TIMESTAMP       NOT NULL,   -- congelado
  CONSTRAINT PK_VALIDACAO_DETALHE PRIMARY KEY (ID)
);
CREATE INDEX IX_VAL_DET_VALIDACAO ON RH_T_VALIDACAO_DETALHE (VALIDACAO_ID, ORDEM);
```

### Justificação das alterações

- **`CAMPO` + `CAMPO_LABEL`** (era só `campo_alterado`) — separa identidade de apresentação. O nome
  técnico permite agrupar/filtrar/re-renderizar; o rótulo preserva **o que foi mostrado ao
  aprovador**. Se o rótulo mudar amanhã, os registos antigos continuam a dizer o que diziam.
- **`ORDEM`** — sem isto a grelha sai pela ordem que a base decidir. A ordem de declaração dos
  campos é intencional; guardá-la custa uma coluna.
- **`TIPO_ALTERACAO`** — mapeia o output do `compare()`: `ValueChange`→`VALOR`,
  `ReferenceChange`→`REFERENCIA`, `InitialValueChange`→`INICIAL`. Deixa o frontend distinguir
  *"criado com Direção X"* de *"Direção X → Y"* sem inferir do `tipoAccao`.
- **`ALTERADO_POR` / `DATA_ALTERACAO`** — **não são deriváveis**. Ao aprovar, a linha `P` passa a
  `A` e o `lastModifiedBy` é sobrescrito com o **aprovador**; a grelha passaria a dizer que quem
  alterou foi quem aprovou.
- **500 → 2000** nos valores — `obs` é campo livre e passa dos 500 com facilidade.
- **Sem `valor_*_raw`** — guardar o id além do nome são duas colunas para uma necessidade de debug
  que a linha `P` já cobre.

### Regras

- **Insert-once, nunca UPDATE.** É registo congelado. Se está errado, corrige-se o cálculo.
- **Idempotência.** `UNIQUE (VALIDACAO_ID, CAMPO, TABELA_ID)` como rede contra duplo clique/retry.

---

## 5. Desenho Java — padrões

Objectivo declarado: **facilitar a vida do chamador**. Cada padrão abaixo existe para eliminar uma
forma de errar, não por elegância.

### 5.1 Builder fluente — declaração dos campos

**Substitui:** `Set<String> camposNegocio()` + `Map<String,String> rotulos()`, hoje duas estruturas
não ordenadas onde o nome do campo é repetido e é fácil pôr um sem o outro.

```java
CamposComparaveis.declarar()
    .campo("tipoSituacao",  "Tipo de mobilidade")
    .campo("dataInicio",    "Data início")
    .campo("dataFim",       "Data fim")
    .referencia("instidId",     "Direcção")       // FK → resolvida para nome legível
    .referencia("secaoId",      "Secção")
    .referencia("localTrabId",  "Local de trabalho")
    .texto("obs",           "Observações")
    .build();
```

Ganhos: **a `ORDEM` sai de graça** (ordem de declaração), o rótulo anda colado ao campo (impossível
declarar um sem o outro), e `referencia()` marca a resolução de FK sem uma segunda lista.

### 5.2 Strategy + Registry — um bean por módulo

Contrato único que cada módulo implementa:

```java
public interface ComparacaoModulo<T> {
  String referenciaName();                       // MOBILIDADE, CARREIRA...
  String tabelaName();                           // RH_T_MOBILIDADE
  CamposComparaveis campos();
  Par<T> estados(ValidacaoEntity validacao);     // devolve (antes, depois)
}
```

O Spring descobre-os por injecção de `List<ComparacaoModulo<?>>` e o serviço indexa-os por
`referenciaName` no `@PostConstruct` — **é o mecanismo que os descritores actuais já usam e que
funciona bem; mantém-se**. Ligar um módulo novo continua a ser adicionar um bean.

### 5.3 Template Method — base para o caso comum

A maioria dos módulos faz o mesmo: buscar a linha pendente por id e a activa do mesmo funcionário.

```java
public abstract class ComparacaoModuloBase<T> implements ComparacaoModulo<T> {
  protected abstract JpaRepository<T, Long> repo();
  protected abstract Optional<T> anterior(T pendente);

  @Override
  public Par<T> estados(ValidacaoEntity v) {
    T pendente = repo().findById(v.getReferenciaId()).orElseThrow(...);
    return new Par<>(anterior(pendente).orElse(null), pendente);   // null = criação
  }
}
```

Um módulo típico fica em ~25 linhas: `referenciaName`, `tabelaName`, `campos()`, `anterior()`.
Casos irregulares (registo de colaborador, que atravessa várias tabelas) implementam
`ComparacaoModulo` directamente, sem a base.

### 5.4 Facade — um ponto de entrada para leitura

```java
@Service
@RequiredArgsConstructor
public class DetalheAlteracoesService {

  public List<ValidacaoDetalheDTO> listar(UUID validacaoUuid) {
    ValidacaoEntity v = validacaoRepo.findByUuidOrThrow(validacaoUuid);
    return v.getEstado() == Estado.P
        ? calcular(v)                                        // compare(antes, depois)
        : detalheRepo.findByValidacaoIdOrderByOrdem(v.getId())
                     .stream().map(this::toDto).toList();
  }
}
```

O chamador (query handler) faz **uma chamada** e não sabe se o resultado foi calculado ou lido.

### 5.5 Execute Around — o congelamento na escrita

O padrão mais importante desta proposta. A ordem *calcular → gravar → consolidar* é o único
invariante que o chamador pode inverter, e invertê-lo produz linhas vazias em silêncio. Em vez de
confiar na disciplina de quem escreve o write-service, **torna-se impossível de inverter**:

```java
@Service
@RequiredArgsConstructor
public class CongelamentoDetalheService {

  public <R> R congelarEExecutar(ValidacaoEntity validacao, Supplier<R> consolidacao) {
    if (!detalheRepo.existsByValidacaoId(validacao.getId())) {   // idempotência
      detalheRepo.saveAll(paraEntidades(detalheService.calcular(validacao), validacao));
    }
    return consolidacao.get();
  }
}
```

Chamador:

```java
return congelamento.congelarEExecutar(validacao, () -> consolidarMobilidade(mobilidade));
```

**Deliberadamente NÃO se usa AOP/anotação aqui.** Uma `@CongelaDetalhe` mágica reintroduziria
exactamente a classe de bug que estamos a eliminar: comportamento invisível que falha calado
quando alguém se esquece. O `Supplier` é explícito, verificado pelo compilador e legível.

### 5.6 O que se elimina

`ValidacaoAuditContext` + filtro, as 16 chamadas a `set()`, os aspectos AOP, `AuthorProvider`,
`CommitPropertiesProvider`, `@JaversSpringDataAuditable` (16 repositórios), `REFERENCIAS_RASAS`,
os dois POJOs de snapshot, `RegistoDetalheCapturaService`, `JaversSqlRepository`.

**O ganho não é performance nem elegância — é que desaparece a classe de bugs silenciosos.** Não há
`set()` para esquecer nem commit por marcar. Se o resolver não devolver dois estados, rebenta alto.

### 5.7 O que se reaproveita

- `ReferenciaNomeResolver` — inteiro, id→nome legível.
- Os 11 descritores — os campos e rótulos migram para o builder fluente; o conhecimento de domínio
  não se perde, muda de forma.
- `ValidacaoDetalheDTO` — acrescenta `campo` e `tipoAlteracao`; `campoAlterado` mantém-se como o
  **rótulo**, portanto **o frontend não parte** (mudanças aditivas).

---

## 6. Plano de migração

Módulo a módulo. O JaVers continua a servir os que ainda não migraram.

1. **Migration Flyway** `V4__validacao_detalhe.sql` — colunas novas + índice + backfill.
2. **Infra nova**: `CamposComparaveis`, `ComparacaoModulo`, `ComparacaoModuloBase`,
   `DetalheAlteracoesService`, `CongelamentoDetalheService`. Bean `Javers` reduzido a
   `JaversBuilder.javers().build()`.
3. **Piloto: mobilidade.** É o módulo com evidência HTTP do arranjo actual — comparar a grelha
   lado a lado, campo a campo.
4. **Restantes 10 módulos.** A partir daqui é repetitivo.
5. **Escalão** dobra-se para dentro do modelo normal; o desvio no `GetDetalheAlteracoesQueryHandler`
   desaparece.
6. **Só quando nenhum módulo depender de commits**: cair aspectos, `ThreadLocal`, anotações e
   repositório SQL. As tabelas `JV_*` ficam a apodrecer até haver confiança para as largar.

### Critério de aceitação

Para cada módulo migrado, a resposta de `GET .../detalhe-alteracoes` tem de ser equivalente à
actual — mesmos campos, mesmos valores antes/depois, mesmos rótulos — para uma validação
**pendente** e para uma **já decidida**.

---

## 7. Riscos

| Risco | Mitigação |
|---|---|
| A linha `A` "anterior" nem sempre é óbvia (histórico, coleções) | é exactamente o que o `anterior()` de cada módulo encapsula; os casos difíceis já estão resolvidos hoje nos descritores |
| Validações **antigas** já decididas não têm detalhe congelado | backfill impossível (o "antes" já não existe); ler do JaVers para essas, por `validacaoUuid`, enquanto as tabelas `JV_*` existirem |
| Registo de colaborador atravessa várias tabelas | implementa `ComparacaoModulo` directamente e devolve vários pares; a base é para o caso simples |
| Escritas por procedure (`PKG_AUMENTO_SALARIAL`) | fora de âmbito — não eram vistas pelo JaVers e não passam a ser |
