# Analise de Impacto: Substituicao de InstituicaoEntity (INPSSIGOF) por Schema Proprio (INPSRH)

**Data:** 2026-06-10  
**Autor:** Equipa RH INPS  
**Status:** Em analise  

---

## 1. Contexto e Objetivo

A entidade `InstituicaoEntity` esta atualmente mapeada para a tabela **`INPSSIGOF.INSTITUICOES`** — um schema externo que pertence ao sistema SIGOF (Sistema Integrado de Gestao Orcamental e Financeira).

O objetivo desta analise e avaliar o impacto de **criar uma tabela propria no schema INPSRH** para parametrizar instituicoes de forma independente, eliminando (ou reduzindo) a dependencia do schema externo INPSSIGOF.

---

## 2. Situacao Atual

### 2.1 Tabela Origem (INPSSIGOF.INSTITUICOES)

| Propriedade | Valor |
|-------------|-------|
| Schema | `INPSSIGOF` |
| Tabela | `INSTITUICOES` |
| Total de colunas | 59 |
| Total de registos | 597 |
| Primary Key | `INSTIT_PK` (coluna `ID`) |

A tabela original possui 59 colunas, incluindo campos de investimento, orcamento, controlo e versioning. No entanto, o codigo Java do RH INPS **utiliza apenas 3 campos**:

- `ID` (NUMBER, PK)
- `NOME` (VARCHAR2 150)
- `CODIGO` (VARCHAR2 200)

### 2.2 Mapeamento JPA Atual

```java
@Table(name = "INSTITUICOES", schema = "INPSSIGOF")
public class InstituicaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "codigo")
    private String codigo;
}
```

### 2.3 Outras Tabelas Externas Referenciadas

Alem de `INSTITUICOES`, o projeto tambem depende de outras tabelas do INPSSIGOF e SIPSGLOBAL:

| Schema | Tabela | Usada em |
|--------|--------|----------|
| INPSSIGOF | `CENTROS_CUSTO` | Views de processamento salarial, dossie, historico laboral |
| INPSSIGOF | `ENTIDADES` | Views de processamento salarial (nome centro custo) |
| INPSSIGOF | `RH_TIPO_MOVIMENTOS` | Views de processamento salarial |
| SIPSGLOBAL | `GLB_T_GEOGRAFIA` | Views de relacao laboral, dossie |
| SIPSGLOBAL | `GLB_T_UPS` | Views de relacao laboral |

---

## 3. Inventario de Dependencias

### 3.1 Tabelas Fisicas no INPSRH com Coluna INSTIT_ID

| Tabela | Coluna | FK Fisica na DB? | Constraint | Registos com Valor |
|--------|--------|------------------|------------|--------------------|
| `RH_T_MOBILIDADE` | `INSTIT_ID` | **Sim** | `FK_MOB_INSTIT` → `INSTIT_PK` | 41 |
| `RH_T_SECAO` | `INSTIT_ID` | **Sim** | `FK_SECAO_INSTIT` → `INSTIT_PK` | 13 |
| `RH_T_RESPONSAVEL` | `INSTIT_ID` | Nao (so JPA) | — | 2 |
| `RH_T_AVD` | `INSTIT_ID` | Nao (so JPA) | — | 9 |
| `RH_T_PARAM_OBJETIVO` | `INSTIT_ID` | Nao (so JPA) | — | 1 |
| `RH_T_PARAM_MANUAL_FUNC` | `INSTIT_ID` | Nao (so JPA) | — | 3 |
| `RH_T_AUMENTO_SALARIAL_DET` | `INSTIT_ID` | Nao | — | ~0 |

**Nota:** `RH_T_AUMENTO_SALARIAL_DET` nao possui entidade JPA mapeada no codigo — a coluna existe apenas na base de dados, sem referencia no Java.

**Total de registos afetados:** ~69 linhas com FKs reais + ~15 em tabelas sem FK fisica.

### 3.2 Views da Base de Dados com JOIN para INPSSIGOF.INSTITUICOES

#### RH_V_RELACAO_LABORAL
- **Referencia:** `INPSSIGOF.INSTITUICOES d1`
- **JOIN:** `c1.INSTIT_ID = d1.ID(+)` (via RH_T_MOBILIDADE)
- **Campos usados:** `d1.NOME` (DIRECAO_DESC), `d1.ID` (DIRECAO_ID)

#### RH_V_HIST_LABORAL
- **Referencia:** `INPSSIGOF.INSTITUICOES P`
- **JOIN:** `MOB.INSTIT_ID = P.ID(+)` e `P.INSTIT_ID = T.ID(+)` (cascata para CENTROS_CUSTO)
- **Campos usados:** `P.NOME` (DIRECAO_DESC, CENTRO_CUSTO_DESC), `P.ID` (DIRECAO_ID)

#### RH_V_DOSSIE
- **Referencia:** `INPSSIGOF.INSTITUICOES D`
- **JOIN:** `MOB.INSTIT_ID = D.ID(+)` e `D.ID = E.INSTIT_ID(+)` (cascata para CENTROS_CUSTO)
- **Campos usados:** `D.NOME` (DIRECAO_DESC, CENTRO_CUSTO_DESC), `D.ID` (DIRECAO_ID)

#### RH_V_LISTA_PROCESSAMENTO
- **Referencia:** `INPSSIGOF.INSTITUICOES c`
- **JOIN:** `b.instit_id = c.id` (via CENTROS_CUSTO)
- **Campos usados:** `c.nome` (DIRECAO), `c.codigo` (CODIGO_CC)

#### RH_V_PROC_SAL_CC_REMUN
- **Referencia:** `INPSSIGOF.INSTITUICOES inst` e `INPSSIGOF.INSTITUICOES loc` (self-join)
- **JOIN:** `mob.instit_id = inst.id(+)` e `SUBSTR(inst.codigo, 1, 14) = loc.codigo(+)`
- **Campos usados:** `inst.codigo` (CD_INSTIT), `loc.nome` (LOCAL_TRAB), `loc.codigo` (CD_LOCAL)
- **ATENCAO:** Logica de self-join por substring do codigo — depende da hierarquia de codigos da tabela original

#### RH_V_PROC_SAL_CC_PAG
- **Referencia:** Mesmo padrao de `RH_V_PROC_SAL_CC_REMUN`
- **JOIN:** `mob.instit_id = inst.id(+)` e `SUBSTR(inst.codigo, 1, 14) = loc.codigo(+)`

#### RH_V_PROC_SAL_CC
- **Referencia:** UNION ALL de `RH_V_PROC_SAL_CC_REMUN` e `RH_V_PROC_SAL_CC_PAG`
- Herda todas as dependencias das duas views acima

#### RH_V_FALTA_MENSAL
- **Referencia:** `INPSSIGOF.INSTITUICOES inst`
- **JOIN:** `LEFT JOIN INPSSIGOF.INSTITUICOES inst ON inst.ID = m.INSTIT_ID` (via RH_T_MOBILIDADE)
- **Campos usados:** `inst.ID` (ID_DIRECAO), `inst.NOME` (NOME_DIRECAO)
- **Contexto:** View de agregacao mensal de faltas por colaborador, com direcao/seccao/ilha

#### RH_V_HORA_EXTRA_MENSAL
- **Referencia:** `INPSSIGOF.INSTITUICOES inst`
- **JOIN:** `LEFT JOIN INPSSIGOF.INSTITUICOES inst ON inst.ID = m.INSTIT_ID` (via RH_T_MOBILIDADE)
- **Campos usados:** `inst.ID` (ID_DIRECAO), `inst.NOME` (NOME_DIRECAO)
- **Contexto:** View de horas extras por colaborador, com valores e percentagens

#### RH_V_RESUMO_ASSIDUIDADE
- **Referencia:** `INPSSIGOF.INSTITUICOES i`
- **JOIN:** `LEFT JOIN INPSSIGOF.INSTITUICOES i ON i.id = m.instit_id` (via RH_T_MOBILIDADE)
- **Campos usados:** `i.id` (ID_DIRECAO), `i.nome` (NOME_DIRECAO)
- **Contexto:** View de resumo mensal de assiduidade (dias, faltas, horas trabalhadas/extras/ausencia)

### 3.3 Funcoes PL/SQL Afetadas

#### GET_NOME_CENTRO_CUSTO

```sql
FUNCTION GET_NOME_CENTRO_CUSTO (P_INSTIT_ID IN NUMBER)
RETURN VARCHAR2
IS
    V_NOME INPSSIGOF.ENTIDADES.NOME%TYPE;
BEGIN
    SELECT nome INTO V_NOME
    FROM (
        SELECT b.nome
        FROM INPSSIGOF.CENTROS_CUSTO a
        JOIN INPSSIGOF.ENTIDADES b ON a.ent_id = b.id
        WHERE a.instit_id = P_INSTIT_ID
        ORDER BY a.id
    )
    WHERE ROWNUM = 1;
    RETURN V_NOME;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
END;
```

Esta funcao e chamada no Java pelo `InstituicaoEntityRepository.getNomeCentroCusto()`.

### 3.4 Packages, Procedures e Funcoes PL/SQL que Referenciam INPSSIGOF

Alem da funcao `GET_NOME_CENTRO_CUSTO`, existem **11 objectos PL/SQL adicionais** no schema INPSRH que referenciam `INPSSIGOF` (incluindo tabelas como `INSTITUICOES`, `CENTROS_CUSTO`, `ENTIDADES`, `RH_TIPO_MOVIMENTOS`, e funcoes como `IUR.CALCULA_IUR`):

| Objecto | Tipo | Referencia INSTITUICOES? | Outras refs INPSSIGOF |
|---------|------|--------------------------|----------------------|
| `GET_NOME_CENTRO_CUSTO` | FUNCTION | Nao (usa CENTROS_CUSTO/ENTIDADES) | `CENTROS_CUSTO`, `ENTIDADES` |
| `RH_F_VALIDACAO` | FUNCTION | Nao | `RH_TIPO_MOVIMENTOS` |
| `CONFIRMAR_PROGRESSAO_PROMO` | PROCEDURE | Nao | `RH_TIPO_MOVIMENTOS`, `IUR.CALCULA_IUR` |
| `PKG_IMPORTAR_DADOS` | PACKAGE | **Sim** (`SELECT FROM INPSSIGOF.INSTITUICOES`) | `CENTROS_CUSTO`, `ENTIDADES` |
| `PKG_AUMENTO_SALARIAL` | PACKAGE | Nao | `RH_TIPO_MOVIMENTOS`, `IUR.CALCULA_IUR` |
| `PROCESSAMENTOSALARIAL` | PACKAGE | **Sim** (JOINs com `INSTITUICOES`, `CENTROS_CUSTO`) | `CENTROS_CUSTO`, `ENTIDADES`, `ORC_FINANC_PROJECTOS`, `IUR`, `ORC_ORG_INST`, `ORC_ORGANICAS`, `COMPROMISSOS`, `PAGAMENTOS`, `GLB_ERRO`, `ORC_CAB_AUTOMATICO` |
| `RH_PROCESSAMENTO_SALARIAL_DB` | PACKAGE | **Sim** (JOINs com `INSTITUICOES`, `CENTROS_CUSTO`) | `CENTROS_CUSTO`, `ITENS_ACTOS`, `ORCAMENTOS`, `ECONOMICAS`, `ORC_FINANC_PROJECTOS`, `IUR`, `COMPROMISSOS`, `PAGAMENTOS`, `RH_TIPO_MOVIMENTOS` |
| `TXT_PROCESSAMENTO_SALARIAL_DB` | PACKAGE | **Sim** (identico ao `RH_PROCESSAMENTO_SALARIAL_DB`) | Mesmas dependencias |
| `RH_PK_GERA_XML_DB` | PACKAGE | **Sim** (`SELECT nome FROM INPSSIGOF.INSTITUICOES`) | `RH_TIPO_MOVIMENTOS`, `ITENS_ACTOS`, `ITENS_TIPO` |
| `RH_PK_SUBSISIO_NATAL_F_DB` | PACKAGE | Nao | `RH_TIPO_MOVIMENTOS` |
| `RH_API_ASSIDUIDADE` | PACKAGE | Nao | `ORC_FINANC_PROJECTOS` |

**ATENCAO CRITICA:** Os packages `PROCESSAMENTOSALARIAL`, `RH_PROCESSAMENTO_SALARIAL_DB` e `TXT_PROCESSAMENTO_SALARIAL_DB` contem dezenas de referencias ao schema INPSSIGOF, incluindo chamadas a funcoes do SIGOF como `IUR.CALCULA_IUR` (calculo de imposto), `ORC_FINANC_PROJECTOS.DA_CAMBIO_DIA` (cambio), e `ORC_CAB_AUTOMATICO.EXECUTA_MOVIMENTO` (cabimentos orcamentais). Estas **nao podem ser substituidas** apenas trocando a tabela INSTITUICOES — sao dependencias funcionais profundas do SIGOF.

### 3.5 Resumo de Views

| View | Referencia INPSSIGOF.INSTITUICOES | Referencia CENTROS_CUSTO/ENTIDADES |
|------|-----------------------------------|------------------------------------|
| `RH_V_RELACAO_LABORAL` | Sim (direta) | Nao |
| `RH_V_HIST_LABORAL` | Sim (direta) | Sim (cascata) |
| `RH_V_DOSSIE` | Sim (direta) | Sim (cascata) |
| `RH_V_LISTA_PROCESSAMENTO` | Sim (via centros_custo) | Sim (direta) |
| `RH_V_PROC_SAL_CC_REMUN` | Sim (self-join) | Sim (direta) |
| `RH_V_PROC_SAL_CC_PAG` | Sim (self-join) | Sim (direta) |
| `RH_V_PROC_SAL_CC` | Sim (herdada) | Sim (herdada) |
| `RH_V_FALTA_MENSAL` | Sim (direta) | Nao |
| `RH_V_HORA_EXTRA_MENSAL` | Sim (direta) | Nao |
| `RH_V_RESUMO_ASSIDUIDADE` | Sim (direta) | Nao |

---

## 4. Inventario de Codigo Java Afetado

### 4.1 Entidade e Infraestrutura Core (6 ficheiros)

| Ficheiro | Descricao da Alteracao |
|----------|------------------------|
| `shared/infrastructure/persistence/entity/InstituicaoEntity.java` | Mudar `schema = "INPSSIGOF"` para schema proprio ou remover. Adicionar colunas extras se necessario |
| `shared/infrastructure/persistence/repository/InstituicaoEntityRepository.java` | Reescrever queries nativas que referenciam `INPSSIGOF.INSTITUICOES` diretamente |
| `shared/infrastructure/mappers/InstituicaoMapper.java` | Ajustar se novos campos forem adicionados a entidade |
| `shared/domain/models/Instituicao.java` | Adicionar campos se a nova tabela tiver mais atributos |
| `shared/infrastructure/persistence/projections/InstituicaoProjection.java` | Verificar compatibilidade com novas queries |
| `.igrpstudio/shared/models/InstituicaoEntity.json` | Atualizar metadata IGRP Studio |

### 4.2 Entidades com @ManyToOne para InstituicaoEntity (6 ficheiros)

| Ficheiro | Campo JPA | @JoinColumn |
|----------|-----------|-------------|
| `shared/infrastructure/persistence/entity/SecaoEntity.java` | `instId` | `instit_id` |
| `shared/infrastructure/persistence/entity/MobilidadeEntity.java` | `instidId` | `instit_id` |
| `shared/infrastructure/persistence/entity/ResponsavelEntity.java` | `institId` | `instit_id` |
| `shared/infrastructure/persistence/entity/AvaliacaoEntity.java` | `institId` | `INSTIT_ID` |
| `shared/infrastructure/persistence/entity/ParamManualFuncaoEntity.java` | `institId` | `INSTIT_ID` |
| `shared/infrastructure/persistence/entity/ParamObjetivoEntity.java` | `institId` | `INSTIT_ID` |

**Nota:** Estes ficheiros nao precisam de alteracao no Java se os IDs da nova tabela forem identicos aos da tabela original. O `@JoinColumn` aponta para a coluna local `INSTIT_ID`.

### 4.3 Services (8 ficheiros)

| Ficheiro | Tipo de Uso |
|----------|-------------|
| `shared/application/service/ParametrizacaoService.java` | `getInstituicoes()`, `getCentroByInstituicao()` |
| `avaliacao/application/services/AvaliacaoService.java` | `findByIdOrThrow()` para definicao de objetivos |
| `configuracao/application/services/SeccaoService.java` | `findByIdOrThrow()` em create/update + Criteria query com `InstituicaoEntity_.id` |
| `configuracao/application/services/ResponsavelService.java` | `findByIdOrThrow()`, associar institution + Criteria queries com `InstituicaoEntity_.NOME` e `InstituicaoEntity_.ID` |
| `configuracao/application/services/ManualFuncaoService.java` | `findByIdOrThrow()` em registar |
| `funcionario/application/service/historicolaboral/HistoricoLaboralWriteService.java` | `entityManager.getReference(InstituicaoEntity.class, ...)` |
| `funcionario/application/service/historicolaboral/HistoricoLaboralReadService.java` | Leitura encadeada `MobilidadeEntity::getSecaoId → SecaoEntity::getInstId → InstituicaoEntity::getNome` |
| `funcionario/application/service/MobilidadeWriteService.java` | `entityManager.getReference(InstituicaoEntity.class, ...)` |

### 4.4 Mappers (3 ficheiros)

| Ficheiro | Tipo de Uso |
|----------|-------------|
| `configuracao/infrastructure/mappers/ComponenteAvaliacaoMapper.java` | `findByIdOrThrow()` quando abrangencia = DIRECAO |
| `parametrizacao/infrastructure/mappers/SecaoMapper.java` | `entityManager.getReference()` + `InstituicaoMapper.toDomain()` |
| `funcionario/infrastructure/mappers/MobilidadeMapper.java` | `entityManager.getReference()` + leitura de `getNome()` |

### 4.5 Criteria API / Strategies (4 ficheiros)

| Ficheiro | Tipo de Uso |
|----------|-------------|
| `transversal/application/strategies/assiduidade/DispensaAssiduidadeStrategy.java` | `Join<MobilidadeEntity, InstituicaoEntity>` via `"instidId"` |
| `transversal/application/strategies/assiduidade/HoraExtraAssiduidadeStrategy.java` | `Join<MobilidadeEntity, InstituicaoEntity>` via `"instidId"` |
| `transversal/application/strategies/assiduidade/FaltaAssiduidadeStrategy.java` | `Join<MobilidadeEntity, InstituicaoEntity>` via `"instidId"` |
| `transversal/application/strategies/assiduidade/FeriasAssiduidadeStrategy.java` | `Join<MobilidadeEntity, InstituicaoEntity>` via `"instidId"` |

### 4.6 Repositorios com Queries JPQL/Nativas (1 ficheiro)

| Ficheiro | Tipo de Uso |
|----------|-------------|
| `shared/infrastructure/persistence/repository/TiposRelacionamentoEntityRepository.java` | Multiplas queries JPQL com `join fetch m.instidId`, predicados `t.mobId.instidId.id`, queries nativas em `RH_V_RELACAO_LABORAL` |

### 4.7 View Entities (2 ficheiros)

| Ficheiro | Descricao |
|----------|-----------|
| `shared/infrastructure/persistence/entity/RhVRelacaoLaboralEntity.java` | Entidade imutavel mapeada para `RH_V_RELACAO_LABORAL` — campos `direcaoDesc`, `direcaoId` |
| `shared/infrastructure/persistence/entity/RhVMapaPessoalEntity.java` | Entidade imutavel mapeada para `RH_V_MAPA_PESSOAL` — campo `direcao` |

---

## 5. Endpoints REST Afetados

### 5.1 Endpoints Diretos de Instituicao

| Metodo | Endpoint | Controller | Descricao |
|--------|----------|------------|-----------|
| GET | `/instituicoes/ativos` | ParametrizacaoController | Lista instituicoes ativas |
| GET | `/instituicoes/{institId}/centros-custo` | ParametrizacaoController | Nome centro de custo por instituicao |

### 5.2 Endpoints com Parametro `direcao` / `direcaoId`

| Modulo | Controller | Endpoints com parametro direcao |
|--------|------------|--------------------------------|
| **Assiduidade** | AssiduidadeController | Picagem, movimentos resumidos, faltas, dispensas, horas-extra, ferias, mapa ferias, detalhe mapa ferias, verificar mapa, exportar direito ferias, exportar mapa ferias |
| **Avaliacao** | AvaliacaoController | Lista avaliacoes (param `direcao`) |
| **Processamento** | ProcessoSalarialController | Lista processamento salarial, subsidio natal, subsidio ferias, colaboradores aumento salarial |
| **Processamento** | FosController | Detalhe FOS XML (param `direcaoId`) |
| **Configuracao** | ResponsavelController | GET/POST responsaveis direcao, pesquisa responsaveis (`nomeInstituicao`, `idInstituicao`) |
| **Relatorio** | RelatorioController | Extrair mapa pessoal (param `direcaoId`) |
| **Funcionario** | AlertaController | Lista alertas (param `direcaoId`) |

**Total estimado:** 15+ endpoints afetados indiretamente (filtram por `direcaoId` que resolve para `INSTIT_ID`).

---

## 6. Analise de Riscos

### 6.1 RISCO ALTO — Views do Processamento Salarial

**Descricao:** As views `RH_V_PROC_SAL_CC_REMUN` e `RH_V_PROC_SAL_CC_PAG` usam uma logica de self-join complexa:

```sql
FROM INPSSIGOF.INSTITUICOES inst,
     INPSSIGOF.INSTITUICOES loc
WHERE mob.instit_id = inst.id(+)
  AND SUBSTR(inst.codigo, 1, 14) = loc.codigo(+)
```

Esta logica depende da **hierarquia de codigos** da tabela original para resolver o "local de trabalho" a partir do codigo da instituicao. Se a nova tabela nao preservar exatamente a mesma estrutura hierarquica de codigos, o processamento salarial completo quebra.

**Mitigacao:** Garantir que a migracao preserve todos os registos com codigos relevantes e a relacao hierarquica entre eles.

### 6.2 RISCO ALTO — Dependencia Cascata com CENTROS_CUSTO e ENTIDADES

**Descricao:** A cadeia `INSTITUICOES → CENTROS_CUSTO → ENTIDADES` (todas em INPSSIGOF) e utilizada em:
- `RH_V_DOSSIE` (dossie do colaborador)
- `RH_V_HIST_LABORAL` (historico laboral)
- `RH_V_LISTA_PROCESSAMENTO` (lista de processamentos)
- `RH_V_PROC_SAL_CC_REMUN` / `RH_V_PROC_SAL_CC_PAG` (folha salarial)
- Funcao `GET_NOME_CENTRO_CUSTO`

Substituir apenas INSTITUICOES sem resolver a dependencia de CENTROS_CUSTO/ENTIDADES deixaria as views numa situacao inconsistente — a nova tabela no INPSRH nao teria centro de custo associado no schema INPSSIGOF.

**Mitigacao:** Opcoes:
1. Trazer tambem CENTROS_CUSTO e ENTIDADES para INPSRH (mais complexo)
2. Manter cross-schema para CENTROS_CUSTO/ENTIDADES (dependencia parcial)
3. Replicar apenas os dados de centro de custo necessarios na nova tabela

### 6.3 RISCO MEDIO — Foreign Keys Cross-Schema

**Descricao:** Existem 2 foreign keys fisicas na base de dados:
- `FK_MOB_INSTIT` (RH_T_MOBILIDADE.INSTIT_ID → INPSSIGOF.INSTITUICOES.ID)
- `FK_SECAO_INSTIT` (RH_T_SECAO.INSTIT_ID → INPSSIGOF.INSTITUICOES.ID)

**Mitigacao:** Dropar as FKs antigas e recriar apontando para a nova tabela. Requer janela de manutencao.

### 6.4 RISCO MEDIO — Migracao de Dados

**Descricao:** ~85 registos em 7 tabelas ja referenciam IDs da tabela INPSSIGOF.INSTITUICOES. Se a nova tabela usar IDs diferentes, todas as referencias quebram.

**Mitigacao:** Manter os mesmos IDs na migracao (INSERT preservando a coluna ID original) ou executar script de atualizacao em todas as tabelas dependentes.

### 6.5 RISCO ALTO — Packages PL/SQL de Processamento Salarial

**Descricao:** Os packages `PROCESSAMENTOSALARIAL`, `RH_PROCESSAMENTO_SALARIAL_DB`, `TXT_PROCESSAMENTO_SALARIAL_DB`, `PKG_IMPORTAR_DADOS` e `RH_PK_GERA_XML_DB` contem queries diretas a `INPSSIGOF.INSTITUICOES`. Alem disso, estes mesmos packages tambem chamam funcoes do SIGOF que **nao podem ser internalizadas**:

- `INPSSIGOF.IUR.CALCULA_IUR` / `CALCULA_IUR_PENSIONISTA` — calculo de imposto sobre rendimento
- `INPSSIGOF.ORC_FINANC_PROJECTOS.DA_CAMBIO_DIA` — taxa de cambio
- `INPSSIGOF.ORC_CAB_AUTOMATICO.EXECUTA_MOVIMENTO` — cabimentos orcamentais
- `INPSSIGOF.GLO_EGOV.ETAPA_AUTORIZA_X_TRATA` — workflow de autorizacao
- `INPSSIGOF.GLB_ERRO.*` — tratamento de erros SIGOF

Isto significa que **mesmo substituindo INSTITUICOES, o schema INPSSIGOF continua a ser uma dependencia obrigatoria** para o processamento salarial funcionar.

**Mitigacao:** Substituir apenas as referencias a `INPSSIGOF.INSTITUICOES` dentro dos packages, mantendo as chamadas a funcoes do SIGOF (IUR, cambio, cabimentos). Testar exaustivamente o ciclo completo de processamento salarial.

### 6.6 RISCO MEDIO — Sincronizacao Futura

**Descricao:** Atualmente, os dados de instituicao sao geridos pelo SIGOF. Ao criar uma tabela propria, perde-se a sincronizacao automatica. Se o SIGOF alterar nomes, codigos ou criar novas instituicoes, o RH ficaria desatualizado.

**Mitigacao:** Implementar mecanismo de sincronizacao (job periodico, evento, ou aceitar gestao independente).

### 6.6 RISCO BAIXO — Codigo Java

**Descricao:** A maioria dos services e mappers usam apenas `findByIdOrThrow()` ou `entityManager.getReference()`. Se os IDs forem preservados, o impacto no Java resume-se a:
- Alterar o `@Table` na entity
- Reescrever 2-3 queries nativas no repository

---

## 7. Matriz de Impacto Resumida

| Categoria | Quantidade | Complexidade |
|-----------|------------|--------------|
| Tabelas fisicas afetadas | 7 | Baixa |
| Foreign keys a migrar | 2 | Baixa |
| Views a recriar | **10** | **Alta** |
| Objectos PL/SQL afetados (funcoes, procedures, packages) | **12** (5 referenciam INSTITUICOES diretamente) | **Alta** |
| Ficheiros Java a alterar | **~25** | Media |
| Endpoints REST impactados | ~15+ | Baixa (indireto) |
| Registos a migrar/verificar | ~85 | Baixa |

---

## 8. Plano de Acao Recomendado

### Fase 1 — Preparacao (Pre-requisito)

1. **Decidir escopo da independencia**: substituir apenas INSTITUICOES ou tambem CENTROS_CUSTO/ENTIDADES
2. **Definir estrategia de IDs**: manter IDs originais (recomendado) ou gerar novos
3. **Definir estrategia de sincronizacao**: job periodico, gestao independente, ou replicacao

### Fase 2 — Base de Dados

4. Criar tabela `RH_T_INSTITUICAO` no schema INPSRH:
   ```sql
   CREATE TABLE INPSRH.RH_T_INSTITUICAO (
       ID          NUMBER        NOT NULL,
       CODIGO      VARCHAR2(200),
       NOME        VARCHAR2(150) NOT NULL,
       NIVEL       NUMBER,
       INSTIT_ID   NUMBER,       -- auto-referencia (instituicao pai)
       SIGLA       VARCHAR2(10),
       ESTADO      VARCHAR2(1)   DEFAULT 'A',
       CONSTRAINT RH_INSTITUICAO_PK PRIMARY KEY (ID)
   );
   ```

5. Migrar dados:
   ```sql
   INSERT INTO INPSRH.RH_T_INSTITUICAO (ID, CODIGO, NOME, NIVEL, INSTIT_ID, SIGLA)
   SELECT ID, CODIGO, NOME, NIVEL, INSTIT_ID, SIGLA
   FROM INPSSIGOF.INSTITUICOES
   WHERE /* filtro das instituicoes relevantes para RH */;
   ```

6. Criar sequence (se necessario para novos registos):
   ```sql
   CREATE SEQUENCE INPSRH.RH_INSTITUICAO_SEQ START WITH 1000 INCREMENT BY 1;
   ```

7. Dropar FKs antigas e recriar:
   ```sql
   ALTER TABLE INPSRH.RH_T_MOBILIDADE DROP CONSTRAINT FK_MOB_INSTIT;
   ALTER TABLE INPSRH.RH_T_MOBILIDADE ADD CONSTRAINT FK_MOB_INSTIT
       FOREIGN KEY (INSTIT_ID) REFERENCES INPSRH.RH_T_INSTITUICAO(ID);

   ALTER TABLE INPSRH.RH_T_SECAO DROP CONSTRAINT FK_SECAO_INSTIT;
   ALTER TABLE INPSRH.RH_T_SECAO ADD CONSTRAINT FK_SECAO_INSTIT
       FOREIGN KEY (INSTIT_ID) REFERENCES INPSRH.RH_T_INSTITUICAO(ID);
   ```

8. Recriar as 10 views substituindo `INPSSIGOF.INSTITUICOES` por `INPSRH.RH_T_INSTITUICAO` (incluindo as 3 views de assiduidade: `RH_V_FALTA_MENSAL`, `RH_V_HORA_EXTRA_MENSAL`, `RH_V_RESUMO_ASSIDUIDADE`)

9. Atualizar funcao `GET_NOME_CENTRO_CUSTO` (dependendo da decisao sobre CENTROS_CUSTO)

### Fase 3 — Codigo Java

10. Alterar `InstituicaoEntity.java`: `@Table(name = "RH_T_INSTITUICAO")` (remover schema ou usar INPSRH)
11. Reescrever queries nativas no `InstituicaoEntityRepository.java`
12. Atualizar `InstituicaoEntity.json` (IGRP Studio)
13. Adicionar novos campos ao domain model e mapper se aplicavel
14. Verificar e testar todos os services, mappers e strategies listados

### Fase 4 — Validacao

15. Testar todos os endpoints listados na seccao 5
16. Validar views de processamento salarial com dados reais
17. Validar dossie, historico laboral e relacao laboral
18. Verificar relatorios de assiduidade e avaliacao

---

## 9. Estimativa de Esforco

| Fase | Estimativa |
|------|------------|
| Fase 1 — Preparacao e decisoes | 1 dia |
| Fase 2 — Base de dados (tabela, migracao, views, funcoes) | 2-3 dias |
| Fase 3 — Codigo Java | 1-2 dias |
| Fase 4 — Testes e validacao | 2-3 dias |
| **Total estimado** | **6-9 dias uteis** |

---

## 10. Recomendacao

A substituicao e **viavel mas de impacto medio-alto**. Os maiores riscos concentram-se nas views de processamento salarial (logica de self-join por codigo) e na dependencia cascata com CENTROS_CUSTO/ENTIDADES.

**Recomendacao:** Avancar com a substituicao mantendo os IDs originais e, numa primeira fase, manter cross-schema parcial para CENTROS_CUSTO/ENTIDADES. Numa segunda fase, avaliar se essas tabelas tambem devem ser internalizadas.
