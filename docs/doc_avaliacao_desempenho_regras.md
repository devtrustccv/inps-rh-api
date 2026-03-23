# Avaliação de Desempenho — Documentação Técnica

> **Stack:** Spring Boot · Spring Data JPA · Jakarta Validation · Oracle
> **Schema:** `INPSRH`
> **Prefixo API:** `/api/v1/avaliacao-desempenho`

---

## 1. VISÃO GERAL

O módulo de **Avaliação de Desempenho** gere o ciclo avaliativo semestral dos colaboradores do INPS. O processo divide-se em dois semestres por ano e envolve quatro intervenientes: o **colaborador**, o **responsável direto**, o **RH** e a **Comissão Executiva**.

### Fluxo do Processo

```
Parametrização → Autoavaliação → Avaliação (Responsável) → Entrevista → Parecer → Avaliação Final
```

### Componentes de Avaliação

| Componente | Âmbito | Origem da descrição |
|---|---|---|
| **Objetivos** | INPS / Direção / Individual | `RH_T_PARAM_OBJETIVO` ou `RH_T_PARAM_MANUAL_FUNC` (se INDIVIDUAL) |
| **Competências Comportamentais** | Por direção | `RH_T_PARAM_OBJETIVO` |
| **Competências Técnicas** | Por cargo | `RH_T_PARAM_OBJETIVO` |
| **Atitude Pessoal** | Todos | `RH_T_PARAM_OBJETIVO` |

### Fórmulas de Cálculo

- **Resultado por linha** = `AVALIACAO × PONDERACAO` (por cada linha de objetivo / competência / atitude pessoal)

- **Resultado Objetivos** = `SUM(RH_T_AVD_OBJECTIVO.AVALIACAO × RH_T_AVD_OBJECTIVO.PONDERACAO)`
- **Resultado Competências Comportamentais** = `SUM(RH_T_AVD_COMPETENCIA.AVALIACAO × RH_T_AVD_COMPETENCIA.PONDERACAO)` onde `COMPONENTE = 'COMPETENCIA_COMPORTAMENTAL'`
- **Resultado Competências Técnicas** = `SUM(RH_T_AVD_COMPETENCIA.AVALIACAO × RH_T_AVD_COMPETENCIA.PONDERACAO)` onde `COMPONENTE = 'COMPETENCIA_TECNICA'`
- **Resultado Competências (combinado)** = `(Resultado Comportamentais × PESO_COMPORTAMENTAIS/100) + (Resultado Técnicas × PESO_TECNICA/100)`
- **Resultado Atitude Pessoal** = `SUM(RH_T_AVD_ATITUDE_PESSOAL.AVALIACAO × RH_T_AVD_ATITUDE_PESSOAL.PONDERACAO)`

- **Avaliação Final Semestral** (`RH_T_AVD.AVALIACAO_FINAL`) = soma dos resultados finais por componente:
  - `(Resultado Objetivos × PONDERACAO_OBJETIVO)` → grava em `RH_T_AVD.AVALIACAO_OBJECTIVO`
  - `+ (Resultado Competências × PONDERACAO_COMPETENCIA)` → grava em `RH_T_AVD.AVALIACAO_COMPETENCIA`
  - `+ (Resultado Atitude Pessoal × PONDERACAO_ATITUDE_PESS)` → grava em `RH_T_AVD.AVALIACAO_ATITUDE_PESS`

- **Avaliação Expressiva Quantitativa** = `SUM(RH_T_AVD.AVALIACAO_FINAL × Ponderação do semestre)` dos 2 semestres
  - A ponderação de cada semestre é configurada externamente e fornecida pelo frontend via endpoint de domínios

- **Avaliação Expressiva Qualitativa** = lookup em `RH_T_PARAM_ESCALA` onde o valor quantitativo se enquadra entre `QUANTITATIVA_DE` e `QUANTITATIVA_ATE` → devolve `QUALITATIVA`

---

## 2. REGRAS DE NEGÓCIO

### Parametrização
- Existe uma única versão de parametrização por ano (`RH_T_PARAM_OBJETIVO_DET.ANO`)
- Ao inicializar uma avaliação, carregam-se sempre os dados da **última versão parametrizada do ano corrente**
- A soma das ponderações globais (Objetivos + Competências + Atitude Pessoal) deve ser 100%
- A soma dos pesos dentro de Competências (Comportamentais + Técnicas) deve ser 100%
- Ao gravar qualquer registo de parametrização (`RH_T_PARAM_OBJETIVO_DET`, `RH_T_PARAM_OBJETIVO`, `RH_T_PARAM_ESCALA`, `RH_T_PARAM_MANUAL_FUNC`), o campo `ESTADO` deve ser inicializado automaticamente com `'A'`
- O formulário de Componentes de Avaliação é um único ecrã composto: cabeçalho (`RH_T_PARAM_OBJETIVO_DET`) + 4 secções de linhas (`RH_T_PARAM_OBJETIVO`): **Objectivos Comuns INPS**, **Competências Comportamentais**, **Competências Técnicas** e **Atitude Pessoal**
- O campo `COMPONENTE` em `RH_T_PARAM_OBJETIVO` é preenchido automaticamente pelo backend conforme a secção: `'OBJETIVO'`, `'COMPETENCIA_COMPORTAMENTAL'`, `'COMPETENCIA_TECNICA'` ou `'ATITUDE_PESSOAL'`
- Se `aplicarATodos = true`, o `cargoId` deve ser `null`; se `false`, `cargoId` é obrigatório
- Para linhas de Objectivos com `abrangencia = 'DIRECAO'`, o campo `institId` é obrigatório

### Definição de Objetivos por Colaborador
- É possível selecionar **mais de um colaborador** simultaneamente no formulário de definição de objetivos (campo `FUN_ID` é multiselect)
- Ao gravar, o sistema cria um registo em `RH_T_AVD` (e respetivas linhas em `RH_T_AVD_OBJECTIVO`, `RH_T_AVD_COMPETENCIA`, `RH_T_AVD_ATITUDE_PESSOAL`) **para cada colaborador selecionado**
- Os dados carregados nas linhas (objetivos, competências, atitude pessoal) são os da última parametrização do ano corrente (`RH_T_PARAM_OBJETIVO_DET.ANO`)

### Origem das Descrições por Componente
- Objetivos com abrangência **INPS** aplicam-se a todos os colaboradores — descrição vem de `RH_T_PARAM_OBJETIVO.DESCRICAO`
- Objetivos com abrangência **DIRECAO** aplicam-se apenas à direção/instituição indicada — descrição vem de `RH_T_PARAM_OBJETIVO.DESCRICAO`
- Objetivos com abrangência **INDIVIDUAL** — descrição vem de `RH_T_PARAM_MANUAL_FUNC.DESCRICAO`, filtrado por cargo e carreira do colaborador
- **Competências Comportamentais** — descrição vem de `RH_T_PARAM_MANUAL_FUNC`, filtrado pelo **cargo do colaborador** (`COMPONENTE = 'COMPETENCIA_COMPORTAMENTAL'`)
- **Competências Técnicas** — descrição vem de `RH_T_PARAM_OBJETIVO.DESCRICAO` (`COMPONENTE = 'COMPETENCIA_TECNICA'`)
- **Atitude Pessoal** — descrição vem de `RH_T_PARAM_OBJETIVO.DESCRICAO` (`COMPONENTE = 'ATITUDE_PESSOAL'`)

### Avaliação — Fluxo e Bloqueios
- A autoavaliação é preenchida pelo colaborador e só pode ser submetida uma vez; após submissão fica bloqueada
- A avaliação pelo responsável só pode ser preenchida após submissão da autoavaliação
- A entrevista e o parecer são registados após a avaliação do responsável estar submetida
- A ação de gravar a avaliação (responsável) atualiza em conjunto: `RH_T_AVD`, `RH_T_AVD_OBJECTIVO`, `RH_T_AVD_COMPETENCIA` e `RH_T_AVD_ATITUDE_PESSOAL`

### Estado da Avaliação (`RH_T_AVD.ESTADO`)
- `'A'` — **Aberto**: avaliação inicializada, ainda sem conclusão de semestre
- `'P'` — **Parcialmente Concluído**: o **1.º semestre** foi concluído; o 2.º ainda não existe ou não está concluído
- `'C'` — **Concluído** (TODO SEMESTRE): ambos os semestres estão concluídos

### Lista de Avaliações
- A listagem deve estar agrupada por: **ano → direção → cargo → colaborador**
- Para cada colaborador mostrar as notas do 1.º e 2.º semestre lado a lado (`RH_T_AVD.SEMESTRE` / `RH_T_AVD.AVALIACAO_FINAL`)
- A nota final exibida na lista é a somatória de `AVALIACAO_FINAL` dos dois semestres (avaliação expressiva quantitativa)

### Avaliação Expressiva Final
- Só é calculada quando existirem os **dois semestres concluídos** para o mesmo colaborador e ano
- A ponderação de cada semestre é configurada externamente e fornecida pelo frontend via endpoint de domínios
- A avaliação qualitativa final é determinada por lookup em `RH_T_PARAM_ESCALA`, verificando em que intervalo (`QUANTITATIVA_DE` ≤ valor ≤ `QUANTITATIVA_ATE`) o valor quantitativo se enquadra → devolve `QUALITATIVA`

### Horas da Entrevista
- `HORA_INICIO_ENTREVISTA` e `HORA_FIM_ENTREVISTA` são `VARCHAR2(5)` na BD — armazenadas como `String` no formato `HH:MM`
- Validar com `@Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")` nos DTOs

---

## 3. ENTITIES JPA

As entities abaixo refletem o estado atual do projeto. Todas estendem `AuditEntity` (`cv.inps.rh.shared.config.AuditEntity`).

**ParamObjetivoDetEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_OBJETIVO_DET")
public class ParamObjetivoDetEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_obj_det")
  @SequenceGenerator(name = "seq_param_obj_det", sequenceName = "SEQ_PARAM_OBJETIVO_DET", allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @Column(name = "ANO", nullable = false)
  private Integer ano;

  @Column(name = "PESO_COMPORTAMENTAIS", precision = 5, scale = 2)
  private BigDecimal pesoComportamentais;

  @Column(name = "PESO_TECNICA", precision = 5, scale = 2)
  private BigDecimal pesoTecnica;

  @Column(name = "PONDERACAO_OBJETIVO", precision = 5, scale = 2)
  private BigDecimal ponderacaoObjetivo;

  @Column(name = "PONDERACAO_COMPETENCIA", precision = 5, scale = 2)
  private BigDecimal ponderacaoCompetencia;

  @Column(name = "PONDERACAO_ATITUDE_PESS", precision = 5, scale = 2)
  private BigDecimal ponderacaoAtitudePess;

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;

  @OneToMany(mappedBy = "paramObjetivoDet", cascade = CascadeType.ALL)
  private List<ParamObjetivoEntity> objetivos;

}
```

**ParamObjetivoEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_OBJETIVO")
public class ParamObjetivoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_objetivo")
  @SequenceGenerator(name = "seq_param_objetivo", sequenceName = "SEQ_PARAM_OBJETIVO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PARAM_OBJ_DET_ID", nullable = false)
  private ParamObjetivoDetEntity paramObjetivoDet;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_IS")
  private ParamCarreiraEntity carreira;

  @Column(name = "NUMERO_ORDEM", nullable = false)
  private Integer numeroOrdem;

  @Column(name = "ABRAGENCIA", length = 100, nullable = false)
  private String abrangencia;              // INPS | DIRECAO | INDIVIDUAL

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private InstituicaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @Column(name = "DESCRICAO", length = 300)
  private String descricao;

  @Column(name = "KPI", length = 300)
  private String kpi;

  @Column(name = "PONDERACAO", nullable = false, precision = 5, scale = 2)
  private BigDecimal ponderacao;

  @Column(name = "COMPONENTE", length = 100, nullable = false)
  private String componente;               // OBJETIVO | COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA | ATITUDE_PESSOAL

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;


}
```

**ParamManualFuncaoEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_MANUAL_FUNC")
public class ParamManualFuncaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_manual_func")
  @SequenceGenerator(name = "seq_manual_func", sequenceName = "SEQ_PARAM_MANUAL_FUNC", allocationSize = 1)
  @Column(name = "ID")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_ID")
  private ParamCarreiraEntity carreira;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private InstituicaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @Column(name = "DESCRICAO", length = 300)
  private String descricao;

  @Column(name = "ESTADO", length = 1)
  private String estado;

  @Column(name = "UUID")
  private UUID uuid;
}
```

**AvaliacaoEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "RH_T_AVD")
@Getter
@Setter
public class AvaliacaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd")
  @SequenceGenerator(name = "seq_avd", sequenceName = "RH_T_AVD_SEQ", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUN_ID", nullable = false)
  private FuncionarioEntity funcionario;

  @Column(name = "ANO", nullable = false)
  private Integer ano;

  @Column(name = "SEMESTRE", length = 1, nullable = false)
  private String semestre;                     // '1' | '2'

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "INSTIT_ID")
  private InstituicaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "SECCAO_ID")
  private SecaoEntity seccaoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARGO_ID")
  private ParamCargoEntity cargo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "CARR_PCCS_ID")
  private ParamCarreiraEntity carreira;

  @Column(name = "ESTADO", length = 1, nullable = false)
  private String estado;                       // 'A' | 'P' | 'C'

  @Column(name = "AVALIACAO_FINAL", precision = 5, scale = 2)
  private Double avaliacaoFinal;

  @Column(name = "PESO_COMPORTAMENTAIS", precision = 5, scale = 2)
  private BigDecimal pesoComportamentais;

  @Column(name = "PESO_TECNICA", precision = 5, scale = 2)
  private BigDecimal pesoTecnica;

  @Column(name = "AVALIACAO_OBJECTIVO", precision = 5, scale = 2)
  private BigDecimal avaliacaoObjectivo;

  @Column(name = "AVALIACAO_COMPETENCIA", precision = 5, scale = 2)
  private BigDecimal avaliacaoCompetencia;

  @Column(name = "AVALIACAO_ATITUDE_PESS", precision = 5, scale = 2)
  private BigDecimal avaliacaoAtitudePess;

  @Column(name = "AVALIACAO_QUALITATIVA", length = 100)
  private String avaliacaoQualitativa;

  @Column(name = "OBSERVACAO_GERAL", length = 500)
  private String observacaoGeral;

  @Column(name = "DESCRICAO_PLANO", length = 500)
  private String descricaoPlano;

  @Column(name = "DATA_INICIO_ENTREVISTA")
  private LocalDate dataInicioEntrevista;

  @Column(name = "HORA_INICIO_ENTREVISTA", length = 5)
  private String horaInicioEntrevista;         // formato HH:MM

  @Column(name = "HORA_FIM_ENTREVISTA", length = 5)
  private String horaFimEntrevista;            // formato HH:MM

  @Column(name = "PARECER_COLABORADOR", length = 500)
  private String parecerColaborador;

  @Column(name = "JUSTIFICACAO_MOTIVO", length = 200)
  private String justificacaoMotivo;

  @Column(name = "OBS_COMISSAO_EXEC", length = 500)
  private String obsComissaoExec;

  @Column(name = "UUID")
  private UUID uuid;

  @OneToMany(mappedBy = "avaliacaoObj", cascade = CascadeType.ALL)
  private List<AvaliacaoObjectivoEntity> objetivos;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoCompetenciaEntity> competencias;

  @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL)
  private List<AvaliacaoAtitudePessoalEntity> atitudesPessoais;
}
```

**AvaliacaoObjectivoEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AVD_OBJECTIVO")
public class AvaliacaoObjectivoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_objectivo")
    @SequenceGenerator(name = "seq_avd_objectivo", sequenceName = "SEQ_AVD_OBJECTIVO", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacaoObj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAM_OBJECTIVO_ID")
    private ParamObjetivoEntity paramObjetivo;

    @Column(name = "NUMERO_ORDEM", nullable = false)
    private Integer numeroOrdem;

    @Column(name = "ABRAGENCIA", length = 100, nullable = false)
    private String abrangencia;

    @Column(name = "OBJECTIVOS", length = 300, nullable = false)
    private String objectivos;

    @Column(name = "KPI", length = 300)
    private String kpi;

    @Column(name = "META", length = 300)
    private String meta;

    @Column(name = "PONDERACAO", precision = 5, scale = 2)
    private BigDecimal ponderacao;

    @Column(name = "AUTO_REALIZADO", length = 200)
    private String autoRealizado;

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

    @Column(name = "REALIZADO", length = 200)
    private String realizado;

    @Column(name = "AVALIACAO", precision = 5, scale = 2)
    private BigDecimal avaliacao;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "UUID")
    private UUID uuid;
}
```

**AvaliacaoCompetenciaEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AVD_COMPETENCIA")
public class AvaliacaoCompetenciaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_competencia")
    @SequenceGenerator(name = "seq_avd_competencia", sequenceName = "SEQ_AVD_COMPETENCIA", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAM_OBJECTIVO_ID", nullable = false)
    private ParamObjetivoEntity paramObjetivo;

    @Column(name = "NUMERO_ORDEM", nullable = false)
    private Integer numeroOrdem;

    @Column(name = "ABRAGENCIA", length = 100, nullable = false)
    private String abrangencia;

    @Column(name = "DESCRICAO", length = 300, nullable = false)
    private String descricao;

    @Column(name = "PONDERACAO", nullable = false, precision = 5, scale = 2)
    private BigDecimal ponderacao;

    @Column(name = "COMPONENTE", length = 100, nullable = false)
    private String componente; // COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA

    @Column(name = "PESO", precision = 5, scale = 2)
    private BigDecimal peso; // peso relativo dentro da subcomponente

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "UUID")
    private UUID uuid;
}
```

**AvaliacaoAtitudePessoalEntity**

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_AVD_ATITUDE_PESSOAL")
public class AvaliacaoAtitudePessoalEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_avd_atitude")
    @SequenceGenerator(name = "seq_avd_atitude", sequenceName = "SEQ_AVD_ATITUDE_PESSOAL", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AVD_ID", nullable = false)
    private AvaliacaoEntity avaliacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARAM_OBJECTIVO_ID", nullable = false)
    private ParamObjetivoEntity paramObjetivo;

    @Column(name = "ABRAGENCIA", length = 100, nullable = false)
    private String abrangencia;

    @Column(name = "PONDERACAO", nullable = false, precision = 5, scale = 2)
    private BigDecimal ponderacao;

    @Column(name = "AUTO_AVALIACAO", precision = 5, scale = 2)
    private BigDecimal autoAvaliacao;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "UUID")
    private UUID uuid;
}
```

---

## 4. REPOSITORIES

```java
@Repository
public interface ParamObjetivoDetEntityRepository extends
        JpaRepository<ParamObjetivoDetEntity, Long>,
        JpaSpecificationExecutor<ParamObjetivoDetEntity> {

    Optional<ParamObjetivoDetEntity> findByUuid(UUID uuid);
}

@Repository
public interface ParamObjetivoEntityRepository extends
        JpaRepository<ParamObjetivoEntity, Long>,
        JpaSpecificationExecutor<ParamObjetivoEntity> {

    Optional<ParamObjetivoEntity> findByUuid(UUID uuid);
}

@Repository
public interface ParamEscalaEntityRepository extends
        JpaRepository<ParamEscalaEntity, Long>,
        JpaSpecificationExecutor<ParamEscalaEntity> {

    Optional<ParamEscalaEntity> findByUuid(UUID uuid);
}

@Repository
public interface ParamManualFuncaoEntityRepository extends
        JpaRepository<ParamManualFuncaoEntity, Long>,
        JpaSpecificationExecutor<ParamManualFuncaoEntity> {

    Optional<ParamManualFuncaoEntity> findByUuid(UUID uuid);
}

@Repository
public interface AvaliacaoEntityRepository extends
        JpaRepository<AvaliacaoEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoEntity> {

    Optional<AvaliacaoEntity> findByUuid(UUID uuid);
}

@Repository
public interface AvaliacaoObjectivoEntityRepository extends
        JpaRepository<AvaliacaoObjectivoEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoObjectivoEntity> {

    Optional<AvaliacaoObjectivoEntity> findByUuid(UUID uuid);
}

@Repository
public interface AvaliacaoCompetenciaEntityRepository extends
        JpaRepository<AvaliacaoCompetenciaEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoCompetenciaEntity> {

    Optional<AvaliacaoCompetenciaEntity> findByUuid(UUID uuid);
}

@Repository
public interface AvaliacaoAtitudePessoalEntityRepository extends
        JpaRepository<AvaliacaoAtitudePessoalEntity, Long>,
        JpaSpecificationExecutor<AvaliacaoAtitudePessoalEntity> {

    Optional<AvaliacaoAtitudePessoalEntity> findByUuid(UUID uuid);
}
```

---

## 5. ENDPOINTS

### 5.1 Parametrização — Componentes de Avaliação

**Controller:** `ComponenteAvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/parametrizacao/componentes`
**Request:** `ComponenteAvaliacaoRequestDTO`
**Response:** `ComponenteAvaliacaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar cabeçalhos (`?ano=`) |
| GET | `/{uuid}` | Detalhe completo com as 4 secções de linhas |
| POST | `/` | Criar cabeçalho + todas as linhas numa única transação |
| PUT | `/{uuid}` | Atualizar cabeçalho + linhas numa única transação |
| DELETE | `/{uuid}` | Remover |

> POST/PUT grava `RH_T_PARAM_OBJETIVO_DET` e todas as linhas em `RH_T_PARAM_OBJETIVO` numa única transação. O campo `COMPONENTE` de cada linha é inferido pela secção da lista (`objectivosInps` → `'OBJETIVO'`, `competenciasComportamentais` → `'COMPETENCIA_COMPORTAMENTAL'`, `competenciasTecnicas` → `'COMPETENCIA_TECNICA'`, `atitudesPessoais` → `'ATITUDE_PESSOAL'`).

---

### 5.2 Parametrização — Escala de Avaliação

**Controller:** `EscalaAvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/parametrizacao/escala`
**Request:** `EscalaAvaliacaoRequestDTO`
**Response:** `EscalaAvaliacaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar níveis da escala |
| GET | `/{uuid}` | Detalhe do nível |
| POST | `/` | Criar nível |
| PUT | `/{uuid}` | Atualizar nível |
| DELETE | `/{uuid}` | Remover nível |

---

### 5.3 Parametrização — Manual de Funções

**Controller:** `ManualFuncaoController`
**Base:** `/api/v1/avaliacao-desempenho/parametrizacao/manual-funcao`
**Request:** `ManualFuncaoRequestDTO`
**Response:** `ManualFuncaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar (`?cargoId=`, `?carrPccsId=`, `?institId=`) |
| GET | `/{uuid}` | Detalhe |
| POST | `/` | Criar |
| PUT | `/{uuid}` | Atualizar |
| DELETE | `/{uuid}` | Remover |

---

### 5.4 Avaliações

**Controller:** `AvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/avaliacoes`
**Request inicializar:** `AvaliacaoInicializarRequestDTO`
**Response resumo (lista):** `AvaliacaoResumoResponseDTO`
**Response detalhe:** `AvaliacaoDetalheResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar agrupado por ano/direção/cargo/colaborador (`?ano=`, `?semestre=`, `?estado=`, `?institId=`, `?cargoId=`, `?funId=`) |
| GET | `/{uuid}` | Detalhe da avaliação (sem linhas — linhas via endpoints filhos) |
| POST | `/` | Inicializar avaliação(ões) — cria um registo por colaborador selecionado e carrega linhas automaticamente da última parametrização |
| POST | `/{uuid}/submeter-auto` | Colaborador submete autoavaliação (bloqueia edição posterior) |
| POST | `/{uuid}/submeter-avaliacao` | Responsável submete avaliação |
| POST | `/{uuid}/concluir` | Concluir avaliação semestral |
| GET | `/{uuid}/avaliacao-final` | Calcular e retornar avaliação final expressiva (requer 2 semestres concluídos) |

---

### 5.5 Objetivos da Avaliação

**Controller:** `ObjetivoAvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/avaliacoes/{avdUuid}/objetivos`
**Response:** `ObjetivoAvaliacaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar objetivos da avaliação |
| GET | `/{uuid}` | Detalhe do objetivo |
| PUT | `/{uuid}/auto` | Gravar autoavaliação — `ObjetivoAutoavaliacaoRequestDTO` (`autoRealizado` + `autoAvaliacao`) |
| PUT | `/{uuid}/avaliacao` | Gravar avaliação do responsável — `ObjetivoAvaliacaoRequestDTO` (`realizado` + `avaliacao`) |

---

### 5.6 Competências da Avaliação

**Controller:** `CompetenciaAvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/avaliacoes/{avdUuid}/competencias`
**Response:** `CompetenciaAvaliacaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar (`?componente=COMPETENCIA_COMPORTAMENTAL` \| `COMPETENCIA_TECNICA`) |
| GET | `/{uuid}` | Detalhe da competência |
| PUT | `/{uuid}/auto` | Gravar nota de autoavaliação — `CompetenciaNotaRequestDTO` |
| PUT | `/{uuid}/avaliacao` | Gravar nota de avaliação do responsável — `CompetenciaNotaRequestDTO` |

---

### 5.7 Atitude Pessoal da Avaliação

**Controller:** `AtitudePessoalAvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/avaliacoes/{avdUuid}/atitude-pessoal`
**Response:** `AtitudePessoalAvaliacaoResponseDTO`

| Método | Path | Descrição |
|---|---|---|
| GET | `/` | Listar itens de atitude pessoal |
| GET | `/{uuid}` | Detalhe |
| PUT | `/{uuid}/auto` | Gravar nota de autoavaliação — `AtitudeNotaRequestDTO` |
| PUT | `/{uuid}/avaliacao` | Gravar nota de avaliação do responsável — `AtitudeNotaRequestDTO` |

---

### 5.8 Entrevista, Parecer & Comissão

**Controller:** `AvaliacaoController`
**Base:** `/api/v1/avaliacao-desempenho/avaliacoes`

| Método | Path | Descrição |
|---|---|---|
| PUT | `/{uuid}/entrevista` | Registar data, horas e observações da entrevista — `EntrevistaRequestDTO` |
| PUT | `/{uuid}/parecer-colaborador` | Registar parecer e justificação do colaborador — `ParecerColaboradorRequestDTO` |
| PUT | `/{uuid}/obs-comissao` | Registar observação da Comissão Executiva — `ObsComissaoRequestDTO` |

---

## 6. DTOs

### 6.1 Parametrização — Componentes de Avaliação

```java
// ══════════════════════════════════════════════════════════
//  REQUEST — hierarquia de DTOs de linha
// ══════════════════════════════════════════════════════════

// ── Classe base — campos comuns a todos os tabs ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public abstract class ParamLinhaBaseRequestDTO {

    private Boolean aplicarATodos;          // true → cargoId deve ser null

    private Long cargoId;                   // obrigatório se aplicarATodos = false

    private Long carrPccsId;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal ponderacao;
}


// ── Objectivos Comuns INPS ──
// Campos extra: numeroOrdem, abrangencia, institId, descricao, kpi

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjectivoInpsLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

    @NotNull
    private Integer numeroOrdem;

    @NotBlank
    private String abrangencia;             // INPS | DIRECAO | INDIVIDUAL

    private Long institId;                  // obrigatório se abrangencia = DIRECAO

    @NotBlank
    private String descricao;

    private String kpi;
}


// ── Competências Comportamentais ──
// Campo extra: numeroOrdem

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaComportamentalLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

    @NotNull
    private Integer numeroOrdem;
    // ABRANGENCIA e COMPONENTE são fixos — preenchidos pelo backend
}


// ── Competências Técnicas ──
// Campo extra: numeroOrdem

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaTecnicaLinhaRequestDTO extends ParamLinhaBaseRequestDTO {

    @NotNull
    private Integer numeroOrdem;
    // ABRANGENCIA e COMPONENTE são fixos — preenchidos pelo backend
}


// ── Atitude Pessoal ──
// Sem campos extras — usa apenas os da classe base

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AtitudePessoalLinhaRequestDTO extends ParamLinhaBaseRequestDTO {
    // ABRANGENCIA e COMPONENTE são fixos — preenchidos pelo backend
}


// ══════════════════════════════════════════════════════════
//  RESPONSE — hierarquia de DTOs de linha
// ══════════════════════════════════════════════════════════

// ── Classe base — campos comuns a todos os tabs ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public abstract class ParamLinhaBaseResponseDTO {
    private Long id;
    private String uuid;
    private Boolean aplicarATodos;
    private Long cargoId;
    private Long carrPccsId;
    private BigDecimal ponderacao;
    private String componente;              // preenchido pelo backend
    private String estado;
}


// ── Objectivos Comuns INPS ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjectivoInpsLinhaResponseDTO extends ParamLinhaBaseResponseDTO {
    private Integer numeroOrdem;
    private String abrangencia;
    private Long institId;
    private String descricao;
    private String kpi;
}


// ── Competências Comportamentais ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaComportamentalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {
    private Integer numeroOrdem;
}


// ── Competências Técnicas ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaTecnicaLinhaResponseDTO extends ParamLinhaBaseResponseDTO {
    private Integer numeroOrdem;
}


// ── Atitude Pessoal ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AtitudePessoalLinhaResponseDTO extends ParamLinhaBaseResponseDTO {
    // sem campos adicionais
}


// ══════════════════════════════════════════════════════════
//  Cabeçalho — request e responses
// ══════════════════════════════════════════════════════════

// ── Request principal ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ComponenteAvaliacaoRequestDTO {

    @NotNull
    private Integer ano;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal pesoComportamentais;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal pesoTecnica;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal ponderacaoObjetivo;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal ponderacaoCompetencia;

    @NotNull @DecimalMin("0") @DecimalMax("100")
    private BigDecimal ponderacaoAtitudePessoal;

    @NotEmpty
    private List<ObjectivoInpsLinhaRequestDTO> objectivosInps;

    @NotEmpty
    private List<CompetenciaComportamentalLinhaRequestDTO> competenciasComportamentais;

    @NotEmpty
    private List<CompetenciaTecnicaLinhaRequestDTO> competenciasTecnicas;

    @NotEmpty
    private List<AtitudePessoalLinhaRequestDTO> atitudesPessoais;
}


// ── Response resumo (listagem) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ComponenteAvaliacaoResumoResponseDTODTO {
    private Long id;
    private String uuid;
    private Integer ano;
    private BigDecimal pesoComportamentais;
    private BigDecimal pesoTecnica;
    private BigDecimal ponderacaoObjetivo;
    private BigDecimal ponderacaoCompetencia;
    private BigDecimal ponderacaoAtitudePessoal;
    private String estado;
}


// ── Response detalhe (GET /{uuid}) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ComponenteAvaliacaoResponseDTO {
    private Long id;
    private String uuid;
    private Integer ano;
    private BigDecimal pesoComportamentais;
    private BigDecimal pesoTecnica;
    private BigDecimal ponderacaoObjetivo;
    private BigDecimal ponderacaoCompetencia;
    private BigDecimal ponderacaoAtitudePessoal;
    private String estado;
    private List<ObjectivoInpsLinhaResponseDTO> objectivosInps;
    private List<CompetenciaComportamentalLinhaResponseDTO> competenciasComportamentais;
    private List<CompetenciaTecnicaLinhaResponseDTO> competenciasTecnicas;
    private List<AtitudePessoalLinhaResponseDTO> atitudesPessoais;
}
```

### 6.2 Parametrização — Escala de Avaliação

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EscalaAvaliacaoRequestDTO {

    @NotNull
    private Integer nivel;

    @NotBlank
    private String qualitativa;

    @NotBlank
    private String descricao;

    @NotNull
    private BigDecimal quantitativaDe;

    @NotNull
    private BigDecimal quantitativaAte;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EscalaAvaliacaoResponseDTO {
    private Long id;
    private String uuid;
    private Integer nivel;
    private String qualitativa;
    private String descricao;
    private BigDecimal quantitativaDe;
    private BigDecimal quantitativaAte;
    private String estado;
}
```

### 6.3 Parametrização — Manual de Funções

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ManualFuncaoRequestDTO {

    @NotNull
    private Long institId;

    private Long seccaoId;

    @NotNull
    private Long cargoId;

    private Long carrPccsId;

    @NotBlank
    private String descricao;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ManualFuncaoResponseDTO {
    private Long id;
    private String uuid;
    private Long institId;
    private Long seccaoId;
    private Long cargoId;
    private Long carrPccsId;
    private String descricao;
    private String estado;
}
```

### 6.4 Avaliações

```java
// ── Inicializar avaliação (suporta múltiplos colaboradores) ──

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoInicializarRequestDTO {

    @NotEmpty
    private List<UUID> funIds;                  // um registo é criado por cada funId

    @NotNull
    private Integer ano;

    @NotBlank
    private String semestre;                    // '1' | '2'

    @NotNull
    private Long institId;

    private Long seccaoId;
    private Long cargoId;
    private Long carrPccsId;
}


// ── Response resumo (para listagem) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoResumoResponseDTO {
    private Long id;
    private String uuid;
    private Long funId;
    private String nomeColaborador;
    private Integer ano;
    private String semestre;
    private Long institId;
    private Long seccaoId;
    private Long cargoId;
    private Long carrPccsId;
    private String estado;
    private BigDecimal avaliacaoFinal;
}


// ── Response detalhe (para GET /{uuid}) — sem listas de componentes ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoDetalheResponseDTO {
    private Long id;
    private String uuid;
    private Long funId;
    private String nomeColaborador;
    private Integer ano;
    private String semestre;
    private Long institId;
    private Long seccaoId;
    private Long cargoId;
    private Long carrPccsId;
    private String estado;
    private BigDecimal avaliacaoFinal;
    private BigDecimal pesoComportamentais;
    private BigDecimal pesoTecnica;
    private BigDecimal avaliacaoObjectivo;
    private BigDecimal avaliacaoCompetencia;
    private BigDecimal avaliacaoAtitudePess;
    private String avaliacaoQualitativa;
    private String observacaoGeral;
    private String descricaoPlano;
    private LocalDate dataInicioEntrevista;
    private String horaInicioEntrevista;        // formato HH:MM
    private String horaFimEntrevista;           // formato HH:MM
    private String parecerColaborador;
    private String justificacaoMotivo;
    private String obsComissaoExec;
}
```

### 6.5 Objetivos da Avaliação

```java
// ── Autoavaliação (PUT /{uuid}/auto) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjetivoAutoavaliacaoRequestDTO {
    private String autoRealizado;

    @NotNull @DecimalMin("0") @DecimalMax("10")
    private BigDecimal autoAvaliacao;
}


// ── Avaliação do responsável (PUT /{uuid}/avaliacao) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjetivoAvaliacaoRequestDTO {
    private String realizado;

    @NotNull @DecimalMin("0") @DecimalMax("10")
    private BigDecimal avaliacao;
}


// ── Response ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObjetivoAvaliacaoResponseDTO {
    private Long id;
    private String uuid;
    private Long avdId;
    private Long paramObjetivoId;
    private Integer numeroOrdem;
    private String abrangencia;
    private String descricao;
    private String kpi;
    private String meta;
    private BigDecimal ponderacao;
    private String autoRealizado;
    private BigDecimal autoAvaliacao;
    private String realizado;
    private BigDecimal avaliacao;
}
```

### 6.6 Competências da Avaliação

```java
// ── Request (usado em /auto e /avaliacao) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaNotaRequestDTO {

    @NotNull @DecimalMin("0") @DecimalMax("10")
    private BigDecimal nota;
}


// ── Response ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class CompetenciaAvaliacaoResponseDTO {
    private Long id;
    private String uuid;
    private Long avdId;
    private Long paramObjetivoId;
    private String componente;                  // COMPETENCIA_COMPORTAMENTAL | COMPETENCIA_TECNICA
    private Integer numeroOrdem;
    private String abrangencia;
    private String descricao;
    private BigDecimal peso;
    private BigDecimal ponderacao;
    private BigDecimal autoAvaliacao;
    private BigDecimal avaliacao;
}
```

### 6.7 Atitude Pessoal da Avaliação

```java
// ── Request (usado em /auto e /avaliacao) ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AtitudeNotaRequestDTO {

    @NotNull @DecimalMin("0") @DecimalMax("10")
    private BigDecimal nota;
}


// ── Response ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AtitudePessoalAvaliacaoResponseDTO {
    private Long id;
    private String uuid;
    private Long avdId;
    private Long paramObjetivoId;
    private Integer numeroOrdem;
    private String abrangencia;
    private String descricao;
    private BigDecimal ponderacao;
    private BigDecimal autoAvaliacao;
    private BigDecimal avaliacao;
}
```

### 6.8 Entrevista, Parecer & Avaliação Final

```java
// ── PUT /{uuid}/entrevista ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class EntrevistaRequestDTO {

    @NotNull
    private LocalDate dataInicioEntrevista;

    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
    private String horaInicioEntrevista;

    @NotBlank
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$")
    private String horaFimEntrevista;

    private String observacaoGeral;
    private String descricaoPlano;
}


// ── PUT /{uuid}/parecer-colaborador ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ParecerColaboradorRequestDTO {

    @NotBlank
    private String parecerColaborador;

    private String justificacaoMotivo;
}


// ── PUT /{uuid}/obs-comissao ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class ObsComissaoRequestDTO {

    @NotBlank
    private String obsComissaoExec;
}


// ── GET /{uuid}/avaliacao-final ──

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoFinalResponseDTO {
    private SemestreResponseDTO semestre1;
    private SemestreResponseDTO semestre2;
    private BigDecimal expressivaQuantitativa;
    private String expressivaQualitativa;       // lookup em RH_T_PARAM_ESCALA
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class SemestreResponseDTO {
    private String semestre;                    // '1' | '2'
    private BigDecimal avaliacaoFinal;
    private BigDecimal ponderacao;
    private BigDecimal classificacao;           // avaliacaoFinal × ponderacao
}
```
