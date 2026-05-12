# JOB Alerta

## Agendamento

O job corre **todos os dias às 6h da manhã** via `@Scheduled`:

```java
@Scheduled(cron = "${alerta.job.cron:0 0 6 * * *}")
```

O Spring Boot lê o cron do `application.properties`. Se a propriedade não existir, usa o valor padrão `0 0 6 * * *` (6h diário). Para mudar o horário sem tocar no código, basta adicionar ao properties:

```properties
alerta.job.cron=0 0 8 * * *    # às 8h
alerta.job.cron=0 0 6 * * MON  # só às segundas
alerta.job.cron=0 */6 * * * *  # a cada 6 horas
```

---

## O que corre em cada execução

```
executarJobAlertas()
  ├── processarRenovacaoContrato()
  ├── processarConversaoContrato()
  └── processarLicencaSemVencimento()
       ├── licenças a expirar (dentro do prazo)
       └── licenças já expiradas
```

---

## Regra de cada alerta

**`RENOVACAO_CONTRATO`**
Busca contratos onde `flgRenovavel = 1`, estado `ATIVO`, e `dataFim` está entre hoje e `hoje + prazo`. Prazo lido de `RH_T_DOMAINS` onde `dominio = CONFIGURACAO_PRAZO` e `referencia = RENOVACAO`.

**`CONVERSAO_CONTRATO`**
Busca contratos originais (sem contrato pai) em estado `ATIVO` cuja `dataInicio` está entre `(hoje - 3 anos - prazo)` e `(hoje - 3 anos)`. Ou seja, alerta quando o contrato está prestes a completar 3 anos. Prazo lido de `referencia = CONVERSAO`.

**`LICENCA_S_VENCIMENTO`**
Busca `SituacaoLaboral` com `tipoSituacao = LICENCA_S_VENCIMENTO`, estado `ATIVO`:
- A expirar → `dataFim BETWEEN hoje AND hoje + prazo`
- Já expirada → `dataFim < hoje`

Prazo lido de `referencia = LICENCA_S_VENCIMENTO`.

---

## Deduplicação

Antes de criar qualquer alerta, o job verifica:

```java
alertaRepository.existsByReferenciaIdAndTipoAlerta(id, tipoAlerta)
```

Se já existe um alerta para aquele registo + tipo, **ignora** — não cria duplicado.

---

## Parametrização dos prazos

Os prazos são **totalmente configuráveis na base de dados**, sem deploy:

| `dominio` | `referencia` | `valor` | Efeito |
|---|---|---|---|
| `CONFIGURACAO_PRAZO` | `RENOVACAO` | `30` | Alerta 30 dias antes do fim do contrato |
| `CONFIGURACAO_PRAZO` | `CONVERSAO` | `15` | Alerta 15 dias antes dos 3 anos |
| `CONFIGURACAO_PRAZO` | `LICENCA_S_VENCIMENTO` | `30` | Alerta 30 dias antes de expirar |

Se um registo não existir na tabela `RH_T_DOMAINS`, o sistema usa o **fallback de 30 dias** definido na constante `DEFAULT_PRAZO_DIAS` em `AlertaWriteService`.

---

## Resumo de parametrização

| O quê | Como mudar | Sem deploy? |
|---|---|---|
| Horário do job | `alerta.job.cron` no `application.properties` | ✅ com restart |
| Prazo de cada alerta | Registo em `RH_T_DOMAINS` | ✅ sem restart |
| Endereço remetente do email | `mail.from` no `application.properties` | ✅ com restart |

---

## Implementação

| Classe | Localização |
|---|---|
| `AlertaWriteService` | `shared/domain/service/AlertaWriteService.java` |
| `OracleEmailService` | `shared/infrastructure/services/OracleEmailService.java` |
| `AlertaEntityRepository` | `shared/infrastructure/persistence/repository/AlertaEntityRepository.java` |
| `ContratoEntityRepository` | `shared/infrastructure/persistence/repository/ContratoEntityRepository.java` |
| `SituacaoLaboralEntityRepository` | `shared/infrastructure/persistence/repository/SituacaoLaboralEntityRepository.java` |
