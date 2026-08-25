> Updated: 2026-08-25 14:45

## Goal

Expor o estado de forma UNIFORME nas respostas do módulo funcionário: par `estado` (código
P/A/I/E/C) + `estadoDesc` (descrição do enum `Estado`). Antes era inconsistente (uns davam
descrição, outros o código, e várias secções não expunham estado nenhum).

## Current state

**FEITO e testado live** (app na porta **8088**, `spring-boot:run` default — NÃO 8089). Commit nesta
sessão em `develop`. `mvn compile` EXIT=0.

- `GET /api/v1/funcionarios/validacoes` → cada item traz `estado`/`estadoDesc` (mas filtro continua
  só `P`, por decisão — ver abaixo). Confirmado.
- `GET /api/v1/funcionarios/{uuid}` (uuid `01a038ed-ac9a-7675-908b-be570e60194c`, funcionário 958902)
  → TODAS as secções expõem `estado`+`estadoDesc`: contactos, endereço, familiares, habilitações,
  dadosContratuais, dadosBancarios. Confirmado (tudo `P`/"Pendente").

## Decisions made — do not re-litigate

- Padrão único: `estado` = `Estado.getCode()`; `estadoDesc` = `Estado.getDescription()`.
- Lista de validações mantém o filtro `estado = P` (NÃO relaxar por agora); expõe o campo na mesma.
- Manifestos `.igrpstudio/funcionario/dto/*.json` atualizados a par de cada DTO (via script Python
  que insere os atributos string mantendo o `id` do manifesto).

## Ficheiros tocados (nesta sessão)

- DTOs (application/dto): Validacao, Contacto, Endereco, AgregadoDependente, ExperienciaProfissional,
  FormacaoProfissional, Habilitacao­Literaria, DadosBancarios, DadosContratuais — todos `...RespDTO`.
- Mappers (infrastructure/mappers): Validacao, Contacto, Endereco, Familiar, ExperienciaProfissional,
  FormacaoFeita, HabilitacaoLiteraria, DadosBancarios, DadosContratuais.
- Manifestos correspondentes em `.igrpstudio/funcionario/dto/`.

## Constraints

- JDK23; `spring-boot:run` sobe na 8088 (não passa `-Dserver.port=8089`). Rebuild: matar processo na
  porta e relançar `mvn -q -DskipTests spring-boot:run > app_run.log 2>&1 &`; esperar
  "Started RhInpsServiceApplication" no log (~1–2 min).
- Get-by-id recebe **UUID**, não o id numérico (id numérico → 400 Invalid UUID).
- NÃO limpar dados de teste (colab 958902/958897; validação 962).

## Open questions

- `formacoesFeitas`/`experienciasProfissionais` vieram vazias no teste — mappers já alinhados mas
  falta confirmar o par estado com dados reais.
- Frontend: confirmar se consome o novo `estado` (código) onde antes recebia descrição em
  contactos/endereço (mudança de valor: "Pendente" → "P").

## Next step

Fechar ciclo P→A: `PUT /api/v1/funcionarios/958902/validar` e reconfirmar que `estadoDesc` passa a
"Ativo" nas secções.
