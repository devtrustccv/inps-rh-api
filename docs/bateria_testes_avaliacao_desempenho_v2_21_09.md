# Bateria de testes v2 — Avaliação de Desempenho (base limpa)

Segunda bateria, com **todos os dados do módulo apagados** (10 tabelas, 200 linhas), a
percorrer os ecrãs pela ordem em que um cliente os usaria e confrontando cada resposta com
as imagens da spec em `docs/imagens de avaliacao de desempenho/`.

Casos positivos e negativos, com verificação em BD por SQL directo depois de cada escrita —
incluindo confirmar que as rejeições não deixaram nada gravado.

## 1. Resultado

**3 bugs** e **2 lacunas de contrato**, todos corrigidos e reconfirmados.

| | |
|---|---|
| Bugs de comportamento | 3 |
| Lacunas face aos ecrãs | 2 |
| Cenários negativos que passaram à primeira | 24 |
| Falsos alarmes meus (erro no teste, não no código) | 3 |

## 2. Bugs encontrados

### B1 — As notas não eram validadas ⚠️ o mais grave
`PUT .../processos-avaliacao/{uuid}` aceitava **qualquer número** como nota.

```
nota = 9999  →  avaliacaoObjectivo = 1201, avaliacaoFinal = 1205, qualitativa = null
nota = -5    →  aceite
```

O resultado não cai em nenhum escalão da escala, por isso a avaliação fica **com nota e sem
classificação qualitativa** — e nada avisa.

**Corrigido**: as notas são validadas contra o domínio `NIVEIS_AVD`, e **todas antes de
qualquer escrita**, para uma nota inválida não deixar o período meio gravado.

```
400 — "Avaliação inválida em objectivo 1: 9999. Níveis aceites: [1, 2, 3, 4, 5] (domínio NIVEIS_AVD)."
```

### B2 — Os pesos comportamental/técnica nunca entravam na conta
`PESO_COMPORTAMENTAIS` e `PESO_TECNICA` são parametrizados, validados (têm de somar 100) e
gravados em cada linha de competência — mas o cálculo **nunca lhes tocava**
(`getPeso()`: 0 ocorrências no serviço).

Consequência: as competências comportamentais somam 100% e as técnicas somam 100%, logo
juntas valiam **200%**. O resultado do período saía fora da escala.

| | Antes | Agora |
|---|---|---|
| Competências | 7.00 | 3.60 |
| Nota do período | **5.14** — fora da escala 0–5, sem qualitativa | **3.78** → BOM |

Conferido à mão: objectivos 3.60×40% + competências (4.0×60% + 3.0×40%)×40% + atitude
4.50×20% = **3.78**, igual ao gravado.

### B3 — Reenviar a definição duplicava os objectivos comuns
Gravar duas vezes o mesmo formulário de objectivos comuns criava avaliações repetidas:

```
INPS     1 → 2 avaliações
DIRECAO  2 → 4 avaliações
```

O guard de idempotência existia **só** para a abrangência INDIVIDUAL.

**Corrigido**: vale para as três. Reenviar reutiliza a avaliação existente, acrescenta os
períodos em falta e devolve o aviso em `alertas`. Verificado com duplo envio das três
abrangências: contagens iguais nos dois envios.

## 3. Lacunas face aos ecrãs

Comparando a grelha de definição com `image13.png`:

### C1 — A grelha não tinha o colaborador nem os períodos
O ecrã mostra a coluna **Colaborador** e o estado como etiqueta. A API devolvia
`nomeColaborador` inexistente e `periodicidade: null`.

**Corrigido**: `funId`, `funUuid`, `nomeColaborador`, `periodicidades[]` e `estadoDescricao`.

### C2 — O filtro Unidade era ignorado
O ecrã tem o filtro **Unidade**; `?seccaoId=` não existia na query e era silenciosamente
descartado — devolvia sempre tudo.

**Corrigido**: `?seccaoId=` filtra (verificado: 5 registos → 2 com `seccaoId=2`, 0 com um id inexistente).

## 4. Falsos alarmes — erros meus no teste, não no código

Registados por honestidade, para não voltarem a consumir tempo:

1. **"Mensagens de erro com acentos partidos"** — os bytes na wire são UTF-8 correcto
   (`\xc3\xa9` = `é`); era o meu terminal.
2. **"DIRECAO sem direção passa"** — o meu payload herdava `institId` do modelo base.
3. **"Os detalhes desapareceram"** — apaguei-os eu ao limpar as medições corrompidas do B1.

## 5. A jornada testada

### Ecrã 1 — Componentes de Avaliação, sistema vazio
| ID | Cenário | Resultado |
|---|---|---|
| J1.1 | lista inicial | 200, `content: []` ✔ |
| J1.2 | `versao-atual` sem nada parametrizado | 404 com mensagem clara ✔ |

### Ecrã 2 — Registar componentes (`image5.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J2.1 | ano em falta | 400 | ✔ |
| J2.2 | periodicidade em falta | 400 | ✔ |
| J2.3 | periodicidade inexistente | 400 | ✔ |
| J2.4 | ponderações ≠ 100 | 400 | ✔ |
| J2.5 | pesos ≠ 100 | 400 | ✔ |
| J2.6 | sem objectivos | 400 | ✔ |
| J2.7 | ponderação negativa | 400 | ✔ |
| J2.10 | objectivo DIRECAO sem `institId` | 400 | ✔ |
| — | **BD após 8 rejeições: 0 linhas** | | ✔ |
| J2.8 | gravar 2026 SEMESTRAL | 201 + 8 linhas, somas 100 por família | ✔ |
| J2.9 | repetir o mesmo ano | 409 | ✔ |

### Ecrã 3 — Escala (`image12.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J3.1 | lista vazia inicial | 200 | ✔ |
| J3.2 | gravar lista vazia | 400 | ✔ |
| J3.3 | intervalo invertido (4→1) | 400 | ✔ |
| J3.4 | intervalos sobrepostos | 400 | ✔ |
| J3.5 | qualitativa fora do domínio | 400 | ✔ |
| J3.6 | nível fora do domínio | 400 | ✔ |
| — | **BD após 5 rejeições: 0 linhas** | | ✔ |
| J3.7 | gravar 5 níveis (0–5) | 200 | ✔ |

### Ecrã 4 — Manual de Funções
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J4.1 | lista vazia | 200 | ✔ |
| J4.2 | sem cargo | 400 | ✔ |
| J4.3 | descrição vazia | 400 | ✔ |
| J4.4 | direção inexistente | 404 | ✔ |
| J4.5 | cargo inexistente | 404 | ✔ |
| J4.6 | criar válido | 201 | ✔ |

### Ecrã 5 — Definição de Objectivos (`image13/14/15.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J5.1 | abrangência inexistente | 400 | ✔ |
| J5.2 | período fora do ciclo | 400 | ✔ |
| J5.3 | DIRECAO sem direção | 400 | ✔ |
| J5.4 | INDIVIDUAL sem colaboradores | 400 | ✔ |
| J5.5 | ano sem parametrização | 404 | ✔ |
| J5.6 | colaborador inexistente | 404 | ✔ |
| J5.7 | `paramId` inexistente | 400 | ✔ |
| — | **BD após 7 rejeições: 0 avaliações** | | ✔ |
| J6.1 | INPS, 2 períodos de uma vez | 200 → 1 avd, `FUN_ID`/`INSTIT_ID` nulos, 2 detalhes | ✔ |
| J6.2 | DIRECAO, 2 direções | 200 → 2 avd, uma por direção | ✔ |
| J6.3 | INDIVIDUAL, 2 colaboradores | 200 → 2 avd | ✔ |
| J6.4 | reenviar INDIVIDUAL | 200 + aviso em `alertas` | ✔ |
| — | manual de funções aplicado nas competências e no objectivo individual | | ✔ |
| J14 | **duplo envio das 3 abrangências** | contagens iguais | ✔ após B3 |

### Ecrã 6 — Autoavaliação (`image21.png`) e Avaliação (`image22.png`)
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J8.1 | abrir o ecrã | cabeçalho com os 6 nomes + componentes | ✔ |
| J9.1 | gravar sem periodicidade | 400 | ✔ |
| J9.2 | período fora do ciclo | 400 | ✔ |
| J9.3 | nota negativa | 400 | ✔ após B1 |
| J9.4 | nota 9999 | 400 | ✔ após B1 |
| J10.1 | autoavaliação S1 | 200, não mexe na nota oficial | ✔ |
| J10.2 | avaliação S1 | 200 → 3.78 BOM | ✔ após B2 |
| J10.3 | avaliação S2 | 200 → 4.76 EXCELENTE, estado `C` | ✔ |

### Ecrã 7 — Tabs
| ID | Cenário | Esperado | Obtido |
|---|---|---|---|
| J11.1 | observação geral sem periodicidade | 400 | ✔ |
| J11.2–4 | observação / parecer / comissão em S1 | 200 | ✔ |
| — | **S2 fica intocado** | isolamento por período | ✔ |

### Ecrã 8 — Grelha e Avaliação Final (`image16.png`)
Grelha pai/filho final, com as etiquetas do ecrã:

```
[Pendente  ] (comum INPS)                  Semestre 1 / Semestre 2
[Pendente  ] (comum DIRECAO) ×2            Semestre 1
[Concluído ] Ivanisa Sofia Delgado Silva   nota=4.27 MUITO_BOM
                Semestre 1   3.78  BOM
                Semestre 2   4.76  EXCELENTE
[Pendente  ] João Carlos Lopes Monteiro    Semestre 1
```

Avaliação final: 3.78×50% + 4.76×50% = **4.27 → MUITO_BOM** ✔

## 6. Por rever

A régua da escala usada nos testes (0–5) foi escolhida por mim para ser compatível com notas
de 1 a 5. **O negócio tem de confirmar** a régua definitiva — dela depende a classificação
qualitativa de todas as avaliações. O código não impõe nenhuma.
