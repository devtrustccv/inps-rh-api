# Plano de implementação — Missão de Serviço (spec 14/09/2026)

**Branch:** `feat/missao-servico-processos` (a partir de `develop`)
**Base:** `Especificação Tecnica Funcional - MISSÃO SERVIÇO_14_09_26.md` + `BASE DADOS_14_09_26.md`
**DDL:** [db/missao_servico_14_09_ddl.sql](db/missao_servico_14_09_ddl.sql) — aplicado em dev a 14/09

---

## 1. O que muda, numa frase

A missão deixa de ter **uma etapa única** e passa a ter **4 processos** (`BILHETE_PASSAGEM`, `SEGURO_VIAGEM`, `AJUDA_CUSTO`, `ALOJAMENTO`) em `RH_T_MISSAO_PROCESSO`, **cada um com a sua etapa**. Tudo o que vem depois da submissão — prestadores, requisição, logística, pareceres, cabimento — pendura-se no processo.

```
Missão (SUBMISSAO, estado A/I)
 ├─ Processo BILHETE_PASSAGEM ── etapa própria
 │    ├─ MISSAO_PRESTADOR (PARAM_PREST_ID)  → REQUISICAO → REQUISICAO_COLAB
 │    ├─ LOGISTICA (MISSAO_PROCESSO_ID)     → LOGISTICA_DET
 │    └─ PROCESSO_DET (pareceres UGAL / Coordenador / Director)
 ├─ Processo SEGURO_VIAGEM ─── …
 ├─ Processo AJUDA_CUSTO ───── …
 └─ Processo ALOJAMENTO ────── … (estado depende do campo "Alojamento")
```

Etapas por processo — domínio `TIPO_PROCESSO_ETAPA` (referência `MISSAO_SERVICO`), já existente na BD:
`PRESTADOR_SERVICO → EMISSAO_REQUISICAO → LOGISTICA → VALIDACAO_UGAL → APROVACAO_RH → CABIMENTO → AUTORIZACAO → PAGAMENTO`

> O domínio tem uma etapa `AUTORIZACAO` própria. Substitui o artifício actual (etapa fica `CABIMENTO` e o ecrã distingue-se pelo `estadoCabimento`).

Domínios já parametrizados e a usar (não *hardcoded*): `TIPO_PROCESSO`, `TIPO_PROCESSO_ETAPA`, `PARECER` (`FAVORAVEL`/`DESFAVORAVEL`), `AVALIACAO_FORNECEDOR` (avaliações, pesos e designações).

---

## 2. Feito (Fase 0)

| | |
|---|---|
| DDL aditivo | 6 tabelas novas (`PARAM_PRESTADOR`, `_DET`, `MISSAO_PROCESSO`, `_DET`, `REQUISICAO_COLAB`, `PRESTADOR_AVAL`), FKs `MISSAO_PROCESSO_ID` em prestador/logística, `PARAM_PREST_ID` em prestador, `ANO` em requisição |
| Mapeamento das colunas já existentes | `ILHA_ID`/`CONCELHO_ID` na submissão; `NR_REQUISACAO` (gerado, anual, por prestador) e `VALOR_TOTAL` na requisição |
| Bug corrigido | a Emissão de Requisição falhava com `ORA-01400` desde que a BD passou a exigir `NR_REQUISACAO` |

---

## 3. Decisões

| # | Pergunta | Estado |
|---|---|---|
| D1 | Dados de dev existentes | ✅ **Apagados** a 14/09 (15 missões, 16 prestadores, 71 logística; sem documentos, notificações nem FKs externas) |
| D2 | Que etapas percorre cada tipo de processo | ✅ `BILHETE_PASSAGEM` e `ALOJAMENTO` fazem o fluxo completo; `SEGURO_VIAGEM` e `AJUDA_CUSTO` começam em `LOGISTICA`. Todos passam por UGAL → Aprovação → Cabimento → Autorização → Pagamento. Percursos definidos num único mapa no código |
| D3 | Parecer desfavorável (UGAL, Coordenador, Director) | ✅ Cada parecer é uma linha nova em `PROCESSO_DET` (histórico). Desfavorável exige observação. UGAL ou Director desfavorável → volta a `LOGISTICA`. Coordenador obrigatório antes do Director, mas não vinculativo |
| D4 | `ESTADO = 'FINALIZADO'` não cabia em `VARCHAR2(1)` | ✅ **Coluna alargada** para `VARCHAR2(20)` (DDL secção 10, aplicado em dev) |
| D5 | Extrair Requisição | ✅ PDF gerado no backend (Thymeleaf + flying-saucer, como a Ordem de Serviço). Guardado no MinIO e em `RH_T_DOCUMENTO` no `NEXT` da Emissão; no `SAVE` é só pré-visualização. `EmailService` ganha envio com anexo |
| D7 | Documento para o frontend | ✅ No fim, `frontend_changes_missao_servico.md` passa a guia por ecrã; o changelog actual vai para uma secção "Histórico" |
| D8 | Quem pode dar cada parecer | ✅ Grava-se quem executou; sem bloqueio por perfil nesta fase (a identidade do utilizador ainda é stub) |
| D6 | Contrato da API por processo (incompatível) | ✅ **Aceite**. Endpoints novos em `/{uuid}/processos/{tipoProcesso}/…`; relatório para o frontend no fim (Fase 12) |

---

## 4. Fases

Cada fase fecha com: compilação, teste live (com autorização por fluxo de escrita), secção em `docs/frontend_changes_missao_servico.md` e commit próprio.

### Fase 1 — Fundações (sem mudança de comportamento)
- Entidades + repositórios: `ParamPrestador(+Det)`, `MissaoProcesso(+Det)`, `MissaoRequisicaoColab`, `MissaoPrestadorAval`.
- Campos novos em `MissaoPrestadorEntity` (`paramPrestId`, `missaoProcessoId`) e `MissaoLogisticaEntity` (`missaoProcessoId`).
- Constantes: `TipoProcesso`, `EtapaProcesso` (ordenada), `ResponsavelParecer`.
- Extrair a guarda de etapa (`exigirEtapaMinima`/`avancarEtapa`) para um componente **por processo** — hoje vive no `MissaoServicoServiceWrite` e opera sobre a missão.

### Fase 2 — Gestão de Prestadores (menu novo, independente do fluxo)
- `POST/PUT /api/v1/missao-servico/prestadores`, `GET` lista (filtros nome/ilha/estado) e `GET /{uuid}`.
- Emails adicionais em `_DET` com sync de array (sem id cria, omitido inactiva).
- Por defeito `ESTADO = 'A'`; `ENT_ID` + nome vêm do lookup de `INPSSIGOF.ENTIDADES`.
- `GET /prestadores/{uuid}/avaliacoes` (Ver Avaliação) — fica vazio até à Fase 8.

### Fase 3 — Submissão cria os processos  *(depende de D1, D2)*
- Primeira gravação cria os 4 processos (`ESTADO = 'A'`; `ALOJAMENTO` conforme o novo campo `alojamento`).
- Editar a submissão com o campo alterado activa/inactiva o processo `ALOJAMENTO`.
- Resposta ganha `processos[]` (tipo, etapa, estado) e "Executado por / Data execução" (auditoria).
- Script de limpeza ou migração de dev conforme D1.

### Fase 4 — Etapa Prestadores Serviço (ex-Análise), por processo
- `GET/PUT /{uuid}/processos/{tipo}/prestadores` — selecciona até 3 `paramPrestId` (limite parametrizável).
- `NEXT`: notifica **todos os emails activos** do prestador (principal + `_DET`), com assunto/corpo editáveis; `PROCESSO.ETAPA → EMISSAO_REQUISICAO`.

### Fase 5 — Emissão de Requisição, por processo
- Uma requisição **por prestador** + `REQUISICAO_COLAB` (N colaboradores) — substitui a linha por par prestador × colaborador.
- `NR_REQUISACAO` passa a ter índice único `(ANO, NR_REQUISACAO)`.
- Anexo da proposta; `VALOR_TOTAL`.
- `GET /{uuid}/processos/{tipo}/requisicoes/{reqUuid}/extrair` (conforme D5): NIF do INPS (`RH_T_DADOS_INSTITUICAO`), prestador (nome/NIF/morada), linhas "tipo a favor de colaborador", total por extenso, elaborado/aprovado por.

### Fase 6 — Logística, por processo
- Um endpoint por tipo: só a secção daquele processo.
- `MISSAO_PROCESSO_ID` preenchido; o prestador vem da `REQUISICAO_COLAB` do processo.
- Conforme D2: ajuda de custo (e seguro) sem prestador → DDL para relaxar `LOGISTICA.MISSAO_PREST_ID`.
- Mantém o reaproveitamento de linhas e anexos já corrigido (ids estáveis).
- `NEXT` → `VALIDACAO_UGAL` + notificação ao colaborador.

### Fase 7 — Validação UGAL *(depende de D3)*
- `GET`: documentos Autorização (anexo da missão), Requisição (extraída) e Fatura (anexo da logística).
- `PUT`: parecer (domínio `PARECER`) + observação → `PROCESSO_DET` com `RESPONSAVEL = 'UGAL'`; `NEXT` → `APROVACAO_RH`.

### Fase 8 — Aprovação RH *(depende de D3)*
- Dois pareceres sequenciais: `COORDENADOR_RH`, depois `DIRECTOR_RH`.
- `NEXT` só é aceite com parecer do Director; chama o cabimento (`gerarCabimentoSgal`, ainda stub) → `CABIMENTO`.
- **Avaliar Prestador:** `POST /{uuid}/prestadores/{missaoPrestUuid}/avaliacao`.
  - Pesos: Qualidade 5, Prazo 15, Produto 40, Resposta 20, Preço 20.
  - Factores: Muito Bom 1, Bom 0,75, Satisfaz 0,5, Mau 0,25.
  - Classe: A > 75, B ]40;75], C ]25;40], D [0;25].
  - Pesos lidos do domínio `AVALIACAO_FORNECEDOR`, com estes valores como fallback.

### Fase 9 — Cabimento, Autorização, Pagamento
- Adaptar à etapa do processo (hoje lêem a etapa da missão).
- Autorização → `PAGAMENTO`; quando todos os processos activos chegam a `PAGAMENTO` → missão finalizada (D4).

### Fase 10 — Listas e cancelamento
- **Lista geral:** sub-lista por processo (tipo, valor total, etapa, link executar).
- **Lista Etapa Missão:** `GET /processos?etapa=&tipoProcesso=` paginada.
- **Cancelar:** inactiva também processos, pareceres, requisição-colab e avaliações; se algum processo passou de `PRESTADOR_SERVICO`, renotifica todos os já notificados (nº missão + motivo).

### Fase 11 — Limpeza do modelo antigo — **revista em 14/09: faseada**

> Feito agora:
> - endpoints antigos por missão marcados como **obsoletos** no Swagger (continuam a funcionar);
> - `PUT /{uuid}/pagamento` passa a exigir a missão `FINALIZADO`;
> - limpeza de BD documentada como pendente (DDL secção 13).
>
> Remover os endpoints e as colunas só **depois de o frontend migrar**, para não partir o `develop`. `MISSAO_SERV_ID` fica: é usado como atalho pelo modelo novo.

Plano original:
- DDL: remover `MISSAO_SERV_ID` de prestador/logística, `ENT_ID/NOME/EMAIL` de prestador, `MISSAO_COLAB_ID` de requisição; tornar `MISSAO_PROCESSO_ID` `NOT NULL`.
- Remover endpoints antigos por missão (`/analise`, `/emissao-requisicao`, `/logistica`, …).
- Actualizar `MISSAO_ENTITIES.md`.

### Fase 12 — Bateria de testes e relatório para o frontend
Testes **live, pela ordem em que o utilizador percorre os ecrãs**. Cada passo:
1. faz o `GET` que o ecrã faria antes;
2. envia o payload que o ecrã enviaria;
3. mostra a resposta crua (HTTP status + JSON);
4. confirma o efeito com um novo `GET`.

Escritas só com autorização por fluxo.

| Ecrã | Happy path | Caminhos não felizes (exemplos) |
|---|---|---|
| Gestão de Prestadores | registar com 2 emails; editar; inactivar um email | sem `entId`/email; email inválido; editar prestador inexistente |
| Nova Missão | nacional (ilha/concelho) e estrangeira; 4 processos criados; alojamento sim/não | `dataFim < dataInicio`; sem colaboradores; ilha com destino estrangeiro (ignorada) |
| Prestadores Serviço | 3 prestadores → `NEXT` notifica todos os emails activos | 4 prestadores; prestador inactivo; `NEXT` fora de etapa |
| Emissão de Requisição | requisição por prestador com N colaboradores; nº anual; PDF extraído | colaborador fora da missão; prestador não seleccionado na etapa anterior; `NEXT` sem colaborador |
| Logística (4 tipos) | um por tipo; cálculo 100% / ⅔ / ⅓ da ajuda de custo; regravar sem duplicar | colaboradores de prestadores diferentes na mesma linha; valores em falta |
| Validação UGAL | favorável → Aprovação | desfavorável sem observação; desfavorável → volta atrás (D3) |
| Aprovação RH | coordenador → director favorável → cabimento | director antes do coordenador; `NEXT` sem parecer do director |
| Cabimento / Autorização / Pagamento | cabimentar → autorizar → pago → missão `FINALIZADO` | autorizar sem cabimento; pagar antes de autorizar |
| Avaliar Prestador | pesos do domínio → total e classe A–D | critério em falta; valor fora do domínio |
| Listas | sub-lista por processo; Lista Etapa filtrada | filtros vazios; paginação no limite |
| Cancelar | antes e depois da etapa de prestadores (renotificação) | cancelar missão já cancelada |

No fim, `docs/frontend_changes_missao_servico.md` recebe o relatório consolidado **por ecrã**: endpoints, payloads, respostas e erros esperados.

### Fora deste plano

**Fase 2 do projecto, segundo a spec:**
- Portal do colaborador — é o único ponto que a spec marca explicitamente como "segunda Fase".

**Bloqueado por falta de contrato:**
- Integração SGAL real.

**Por implementar — a spec pede, e este plano adiou sem base (corrigido a 2026-09-15):**
- **Job de alertas.** A spec lista "Implementar job que regista alerta (alertas descritas acima)"
  como acção, a par de "Enviar notificação em cada uma das etapas", que foi implementada. A infra
  existe e está provada: `AlertaWriteService.executarJobAlertas()`, `@Scheduled` diário às 6h, com
  três tipos já a funcionar (renovação e conversão de contrato, licença sem vencimento).
  Faltam os casos da missão — mas **só um dos seis é construível com o modelo actual**:

  | Alerta | Dados necessários | Estado |
  |---|---|---|
  | Missão próxima do início sem confirmação | `dataInicio` + linhas de logística | ✅ construível hoje |
  | Fatura próxima do vencimento | data de vencimento da fatura | ❌ não existe: a fatura é um anexo em `RH_T_DOCUMENTO`, que não tem datas |
  | Fatura em atraso | idem | ❌ idem |
  | Fatura em falta | data da requisição + prazo | ⚠️ a data existe (auditoria); o prazo não está parametrizado |
  | Requisição pendente de resposta | envio + confirmação da agência | ⚠️ o envio existe; **não há conceito de "agência confirmou"** |
  | Documentos obrigatórios em falta | lista do que é obrigatório por etapa | ⚠️ por definir com o negócio |

- **Parametrização de prazos e limiares.** A própria spec diz que é "essencial para alertas baseados
  em tempo ou valores". Sem ela, os limiares dos alertas ficariam no código.

---

## 5. Ordem e dependências

```
F1 ─┬─ F2 (paralelo)
    └─ F3* ── F4 ── F5 ── F6* ── F7* ── F8* ── F9 ── F10 ── F11 ── F12
                                   (* depende de D2/D3/D5)
```

F2 não bloqueia nada e pode avançar enquanto as decisões D1–D6 não chegam.
