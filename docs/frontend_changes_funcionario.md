# Alterações Front-End — Funcionário / Dossiê (ciclo CORRIGIR — estado "C")

**Data:** 2026-08-22
**Branch:** feat/dossie-corrigir-javers-restantes

---

## 1. Novo campo `direcaoAntesId` no `MobilidadeDTO`

Aplica-se à resposta de:
- `GET` detalhe/atual de mobilidade (usa `MobilidadeDTO`).

### 1.1 Campo `direcaoAntesId`

| | Valor |
|---|---|
| **Nome** | `direcaoAntesId` |
| **Tipo** | `Long` (id da direcção) |
| **Acesso** | Só leitura (não enviar no corpo do pedido) |

Complementa o já existente `dirrecaoAntes` (nome/descrição, `String`) com o **id** da direcção "Antes".
O identificador é o `Long id` — a `DirecaoEntity` **não tem uuid** —, o mesmo espaço de chaves de
`direcaoDepois` e dos selects do formulário, para permitir comparação directa "antes → depois".

---

## 2. Listas passam a refletir o estado de workflow "C" (em correção)

Contexto: os registos devolvidos ao maker ficam em estado **C** (em correção). Algumas listas liam de
vistas que não expunham o C, pelo que um registo devolvido aparecia como "Ativo" ou desaparecia. Corrigido:

- **Lista de Carreira** (`GET .../carreiras`): o estado passa a mostrar **P/C** corretamente (a vista
  colapsava C→A; agora sobrepõe-se com o estado real da tabela). Sem alteração de contrato de API — só o
  valor de `estado`/`estadoDesc` fica correto.
- **Lista de Contrato / Gestão Contratual** (`GET .../contratos`): uma **renovação** devolvida para
  correção (histórico em C) deixava de aparecer; agora o filtro inclui **C** e ela mantém-se visível.
  Sem alteração de contrato de API.

> Já corretos (sem alteração): lista de Mobilidade (já sobrepunha P/C) e lista de Dossiê/Colaboradores
> (já incluía A, P, C, I no filtro).

Valores possíveis de estado nas listas: `A` (Ativo), `P` (Pendente), `C` (Em correção), `I` (Inativo).
