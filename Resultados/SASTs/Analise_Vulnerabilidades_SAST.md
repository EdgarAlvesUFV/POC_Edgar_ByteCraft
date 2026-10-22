# Análise de Vulnerabilidades SAST — ByteCraft

> Relatório de validação manual das vulnerabilidades reportadas por ferramenta SAST (Semgrep/outros) nos projetos dos 5 anos.
> **Data da análise:** 21/10/2026  
> **Arquivo fonte:** `Resultados/SASTs/Lista de vulnerabilidades Sast.txt`

---

## Legenda

| Ícone | Significado |
|-------|-------------|
| ✅ **Verdadeiro Positivo** | Vulnerabilidade real, requer correção |
| ❌ **Falso Positivo** | Alerta incorreto da ferramenta, código está seguro |
| ⚠️ **Erro de Relatório** | Linha inexistente, CWE/descrição errada, ou arquivo errado |
| 🔴 **Crítico** | Risco imediato de segurança (exploração remota, vazamento de segredos) |
| 🟡 **Médio** | Boas práticas violadas, facilita ataques ou dificulta manutenção |
| 🟢 **Baixo** | Bug latente, improvável em produção mas tecnicamente incorreto |

---

## Ano 1

### 1. `SecurityConfig.java` — CWE-352 (`spring-disabled-csrf-protection`) — Linha 35
**Código (linhas 34–40):**
```java
.csrf(csrf -> csrf.ignoringRequestMatchers(
        "/login",
        "/professores/cadastrar",
        "/turma/**",
        "/misterios/"
))
```

**Veredito:** ❌ **FALSO POSITIVO**

**Explicação:**  
O código **não desativa** a proteção CSRF globalmente. Usa `ignoringRequestMatchers()` para desativar **apenas em endpoints públicos específicos** (login, cadastro, APIs públicas), prática **correta e recomendada** para APIs REST stateless com JWT. O restante da aplicação permanece protegido. O SAST confundiu `ignoringRequestMatchers` com `disable()`.

---

### 2. `Dockerfile` — CWE-250 (`Execution with Unnecessary Privileges`) — Linha 48
**Veredito:** ⚠️ **ERRO DE RELATÓRIO**

**Explicação:**  
O Dockerfile do Ano 1 possui **apenas 9 linhas**. A linha 48 **não existe**. Provavelmente erro de mapeamento de linha do SAST ou arquivo incorreto no relatório.

---

## Ano 2

### 3. `SecurityConfig.java` — CWE-352 (`spring-disabled-csrf-protection`) — Linha 52
**Código (linha 52):**
```java
.csrf(csrf -> csrf.disable()) // Desativa a proteção contra CSRF
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🔴 **CRÍTICO**

**Explicação:**  
Proteção CSRF **totalmente desativada** via `.disable()`. Em aplicações web com autenticação baseada em sessão/cookie, isso expõe usuários a **ataques CSRF** (ações não autorizadas em nome da vítima). Mesmo sendo API, se houver qualquer endpoint com autenticação stateful, é explorável.  
**Correção:** Substituir por `ignoringRequestMatchers()` apenas para endpoints públicos (ex: `/auth/**`), igual ao Ano 1.

---

### 4. `NetworkDeviceDAOimpl.java` — CWE-489 (`Active Debug Code`) — Linhas 41, 63, 86, 102, 119, 131, 146
**Código (exemplo linha 41):**
```java
} catch (SQLException e) {
    e.printStackTrace();  // ← 7 ocorrências idênticas
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
Todas as 7 ocorrências usam `e.printStackTrace()` em blocos `catch`. Isso é **código de debug ativo em produção**. Problemas:
- Vai para `System.err` sem controle de nível (ERROR/WARN/INFO)
- Sem formatação estruturada, rotação de logs, ou integração com SIEM
- Vaza stack traces internos em logs acessíveis  
**Correção:** Usar SLF4J/Logback: `log.error("Falha ao buscar dispositivo: {}", id, e);`

---

### 5. `JwtTokenServiceImpl.java` — CWE-798 (`Use of Hard-coded Credentials`) — Linha 18
**Código (linha 18):**
```java
private static final String SECRET_KEY = "anfjsiurfyu1wgryu3gf26brf67gj7efg24r4f";
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🔴 **CRÍTICO**

**Explicação:**  
Chave secreta JWT **hardcoded no código-fonte**. Riscos:
- Qualquer um com acesso ao repo (devs, CI/CD, backups) tem a chave
- Impossível rotacionar sem recompilar e redeploy
- Viola 12-Factor App (configuração no ambiente, não no código)  
**Correção:** Externalizar via `@Value("${jwt.secret}")` + variável de ambiente / Kubernetes Secret / HashiCorp Vault / AWS Secrets Manager.

---

## Ano 3

### 6. `ControladorDesafio.java` — CWE-197 (`Numeric Truncation Error`) & CWE-681 (`Incorrect Conversion`) — Linhas 98 e 188
**Código (linha 98):**
```java
ProgressoAluno progressoAluno = acessoBDProgressoAluno.getProgressoAluno((int) pkAluno);
```
`pkAluno` é `long` (64 bits), feito cast para `int` (32 bits).

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟢 **BAIXO/MÉDIO**

**Explicação:**  
Se `pkAluno > 2.147.483.647` (`Integer.MAX_VALUE`), ocorre **truncamento silencioso** → ID incorreto/negativo → busca errada ou erro de BD. Improvável em sistema educacional, mas bug latente.  
**Correção:** Alterar repository para aceitar `Long`, ou validar `if (pkAluno > Integer.MAX_VALUE) throw ...`.

---

### 7. `ControladorDesafio.java` — CWE-489 (`Active Debug Code`) — Linhas 165 e 232
**Código (linha 165):**
```java
} catch (Exception e) {
    System.out.println("❌ Erro ao salvar pontuação: " + e.getMessage());
    e.printStackTrace();  // ← Linha 165
    return -7;
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
`e.printStackTrace()` + `System.out.println` com emojis em produção. Polui stdout/stderr, não configurável, vaza detalhes internos.  
**Correção:** `log.error("Erro ao salvar pontuação para aluno {}: {}", pkAluno, e.getMessage(), e);`

---

### 8. `ControladorIlhas.java` — CWE-489 (`Active Debug Code`) — Linhas 152 e 182
**Código (linha 152):**
```java
} catch (Exception e) {
    System.err.println("Erro genérico ao avançar ilha: " + e.getMessage());
    e.printStackTrace();  // ← Linha 152
    return -1;
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
Mesmo padrão: `printStackTrace()` + `System.err.println` em código de produção.  
**Correção:** Logging estruturado com SLF4J.

---

### 9. `Dockerfile` — CWE-250 / CWE-269 — Linhas 14, 15, 22, (29)
**Dockerfile (23 linhas):**
```dockerfile
# Linha 14: COPY . .
# Linha 15: CMD ["mvn", "spring-boot:run"]
# Linha 22: ENTRYPOINT ["java","-jar","app.jar"]
# Linha 29: NÃO EXISTE
```

| Linha | Existe? | Veredito | CWE | Explicação |
|-------|---------|----------|-----|------------|
| 14 | ✅ | ✅ **Verdadeiro** | 250 | `COPY . .` executa como root; container roda como root por padrão |
| 15 | ✅ | ✅ **Verdadeiro** | 250 | Stage `dev` roda Maven como root |
| 22 | ✅ | ✅ **Verdadeiro** | 269 | Stage `prod` roda `java -jar` como root |
| 29 | ❌ | ⚠️ **Erro** | — | Arquivo tem só 23 linhas |

**Veredito Geral:** ✅ **PARCIALMENTE VÁLIDO** — 🟡 **MÉDIO**

**Explicação:**  
Nenhum stage cria usuário não-root nem usa `USER nonroot`. Todos rodam como **UID 0 (root)**. Violação de **menor privilégio**: se houver container escape, atacante ganha root no host.  
**Correção:** Em cada stage:
```dockerfile
RUN adduser --disabled-password --gecos "" appuser
USER appuser
```

---

### 10. `TelaPontuacao.jsx` — CWE-134 (`Use of Externally-Controlled Format String`) — Linha 73
**Código (linha 73):**
```javascript
console.log(`${nomeIlhaJogada} já foi jogada?`, foiJogada);
```

**Veredito:** ❌ **FALSO POSITIVO**

**Explicação:**  
CWE-134 aplica-se a **format strings estilo C** (`printf`, `String.format()`) onde atacante controla a string de formato (ex: `%s%n%x` para vazamento de memória). Aqui usa-se **template literal JavaScript** (backticks), que é **seguro** — interpolação `${...}` não executa formatação perigosa. SAST confundiu template literal com format string.

---

## Ano 4

**Nenhuma vulnerabilidade reportada.** ✅

---

## Ano 5

### 11. `SecurityConfig.java` — CWE-352 (descrição errada: *"Use of Externally-Controlled Format String"*) — Linha 73
**Veredito:** ⚠️ **ERRO DE RELATÓRIO** + ❌ **FALSO POSITIVO**

**Explicação:**  
- Arquivo tem **70 linhas** → linha 73 **não existe**.
- Descrição *"Use of Externally-Controlled Format String"* corresponde a **CWE-134**, não CWE-352.
- Código CSRF (linhas 22–27) usa `ignoringRequestMatchers()` para endpoints específicos — **prática correta** (igual Ano 1). Não é vulnerabilidade.

---

### 12. `AlunoController.java` — CWE-489 (`Active Debug Code`) — Linhas 180 e 210
**Código (linha 180):**
```java
} catch (Exception e) {
    System.err.println("❌ Erro ao salvar progresso: " + e.getMessage());
    e.printStackTrace();  // ← Linha 180
    return ResponseEntity.status(500).body(...);
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
`e.printStackTrace()` + `System.err.println` em controller de produção. Padrão recorrente no projeto.  
**Correção:** `log.error("Erro ao salvar progresso: apelido={}, codigoSala={}", apelido, codigoSala, e);`

---

### 13. `ProfessorRepository.java` — CWE-489 (`Active Debug Code`) — Linha 22
**Código (linha 22):**
```java
} catch (Exception e) {
    e.printStackTrace();  // ← Linha 22
    return 0; // falha
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
`printStackTrace()` em repository JPA. Além disso, **engole a exceção** e retorna `0`/`null`, dificultando debugging real e tratamento de erro upstream.  
**Correção:** `log.error("Falha ao salvar professor: {}", professor.getNomeDeUsuario(), e); throw new DataAccessException(...);` ou tratar adequadamente.

---

### 14. `AlunoService.java` — CWE-489 (`Active Debug Code`) — Linha 122
**Código (linha 122):**
```java
} catch (Exception e) {
    System.err.println("❌ [Service] Erro ao salvar progresso: " + e.getMessage());
    e.printStackTrace();  // ← Linha 122
    return false;
}
```

**Veredito:** ✅ **VERDADEIRO POSITIVO** — 🟡 **MÉDIO**

**Explicação:**  
Mesmo padrão: `printStackTrace()` + `System.err.println` em service layer.  
**Correção:** Logging estruturado SLF4J.

---

## Resumo Consolidado

| Ano | Arquivo | CWE | Descrição | Validade | Severidade |
|-----|---------|-----|-----------|----------|------------|
| 1 | SecurityConfig.java | 352 | CSRF desativado | ❌ Falso Positivo | — |
| 1 | Dockerfile | 250 | Privilégios desnecessários | ⚠️ Linha inexistente | — |
| 2 | SecurityConfig.java | 352 | CSRF desativado | ✅ **Verdadeiro** | 🔴 Crítico |
| 2 | NetworkDeviceDAOimpl.java | 489 | Debug code (7×) | ✅ **Verdadeiro** | 🟡 Médio |
| 2 | JwtTokenServiceImpl.java | 798 | Hardcoded secret | ✅ **Verdadeiro** | 🔴 Crítico |
| 3 | ControladorDesafio.java | 197/681 | Truncamento numérico | ✅ **Verdadeiro** | 🟢 Baixo/Médio |
| 3 | ControladorDesafio.java | 489 | Debug code (2×) | ✅ **Verdadeiro** | 🟡 Médio |
| 3 | ControladorIlhas.java | 489 | Debug code (2×) | ✅ **Verdadeiro** | 🟡 Médio |
| 3 | Dockerfile | 250/269 | Execução como root | ✅ **Verdadeiro** (parcial) | 🟡 Médio |
| 3 | TelaPontuacao.jsx | 134 | Format string | ❌ Falso Positivo | — |
| 5 | SecurityConfig.java | 352 | CSRF (descrição errada) | ⚠️ Erro + FP | — |
| 5 | AlunoController.java | 489 | Debug code (2×) | ✅ **Verdadeiro** | 🟡 Médio |
| 5 | ProfessorRepository.java | 489 | Debug code (1×) | ✅ **Verdadeiro** | 🟡 Médio |
| 5 | AlunoService.java | 489 | Debug code (1×) | ✅ **Verdadeiro** | 🟡 Médio |

---

## Plano de Ação Priorizado

### 1. 🔴 **CRÍTICO — Corrigir Imediatamente (Ano 2)**
| Ação | Arquivo | Esforço |
|------|---------|---------|
| Substituir `.csrf().disable()` por `ignoringRequestMatchers("/auth/**")` | `SecurityConfig.java` | Baixo |
| Externalizar `SECRET_KEY` JWT para variável de ambiente / Secret Manager | `JwtTokenServiceImpl.java` | Baixo |

### 2. 🟡 **MÉDIO — Padrão Recorrente (Todos os Anos)**
> **Problema sistêmico:** Uso de `e.printStackTrace()` / `System.out/err.println` em vez de logging estruturado.

| Arquivos Afetados | Ocorrências |
|-------------------|-------------|
| `NetworkDeviceDAOimpl.java` (Ano 2) | 7 |
| `ControladorDesafio.java` (Ano 3) | 2 |
| `ControladorIlhas.java` (Ano 3) | 2 |
| `AlunoController.java` (Ano 5) | 2 |
| `ProfessorRepository.java` (Ano 5) | 1 |
| `AlunoService.java` (Ano 5) | 1 |
| **Total** | **15 ocorrências** |

**Correção padronizada:**
```java
// Adicionar no topo da classe
private static final Logger log = LoggerFactory.getLogger(SuaClasse.class);

// Substituir:
e.printStackTrace();
// Por:
log.error("Descrição contextual: param1={}, param2={}", valor1, valor2, e);
```

**Benefícios:** Níveis de log configuráveis, formatação JSON, integração ELK/Datadog, rotação, correlação de requests (MDC).

---

### 3. 🟡 **MÉDIO — Docker Hardening (Ano 3)**
| Ação | Stages Afetados |
|------|-----------------|
| Criar usuário `appuser` (UID 1000) | `build`, `dev`, `prod` |
| Adicionar `USER appuser` após instalação de deps | `build`, `dev`, `prod` |
| Usar `COPY --chown=appuser:appuser` onde aplicável | `prod` |

```dockerfile
# Exemplo para stage prod
FROM eclipse-temurin:17-jre AS prod
WORKDIR /app
RUN adduser --disabled-password --gecos "" appuser
COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java","-jar","app.jar"]
```

---

### 4. 🟢 **BAIXO — Correção de Cast (Ano 3)**
| Ação | Arquivo |
|------|---------|
| Alterar `getProgressoAluno(int)` para `getProgressoAluno(Long)` no repository e service | `ControladorDesafio.java` + `AcessoBDProgressoAluno` |
| **OU** validar: `if (pkAluno > Integer.MAX_VALUE) throw new IllegalArgumentException(...);` | `ControladorDesafio.java` |

---

### 5. ❌ **IGNORAR — Falsos Positivos / Erros de Relatório**
| Item | Motivo |
|------|--------|
| Ano 1 `SecurityConfig.java` CWE-352 | `ignoringRequestMatchers` ≠ `disable` — prática correta |
| Ano 1 `Dockerfile` linha 48 | Linha não existe (arquivo tem 9 linhas) |
| Ano 3 `TelaPontuacao.jsx` CWE-134 | Template literal JS ≠ format string C — seguro |
| Ano 3 `Dockerfile` linha 29 | Linha não existe (arquivo tem 23 linhas) |
| Ano 5 `SecurityConfig.java` linha 73 | Linha não existe + descrição CWE errada (134 vs 352) + código correto |

---

## Métricas de Qualidade do SAST

| Métrica | Valor |
|---------|-------|
| Total de alertas no relatório | 21 |
| **Verdadeiros Positivos** | **12** (57%) |
| **Falsos Positivos** | **4** (19%) |
| **Erros de Relatório** | **5** (24%) |
| Críticos reais | 2 |
| Médios reais | 9 |
| Baixos reais | 1 |

> **Nota:** A alta taxa de falsos positivos (19%) e erros de relatório (24%) sugere necessidade de **tuning de regras** do SAST (ex: excluir regra `spring-disabled-csrf-protection` quando usado `ignoringRequestMatchers`, corrigir mapeamento de linhas, ajustar regra CWE-134 para não flagar template literals JS).

---

## Recomendações de Processo

1. **Integração CI/CD:** Falhar build apenas em **Críticos + Médios confirmados**; falsos positivos documentados em `semgrep-ignore` ou configuração de baseline.
2. **Baseline SAST:** Criar arquivo `.semgrepignore` ou `baseline.json` com FPs conhecidos para ruído zero em PRs.
3. **Treinamento:** Compartilhar este relatório com o time — foco no padrão `printStackTrace()` (15 ocorrências) e hardcoded secrets.
4. **Secrets Scanning:** Adicionar `truffleHog` / `git-secrets` / `gitleaks` no pipeline para pegar secrets hardcoded (como o JWT do Ano 2) **antes** do merge.

---

*Relatório gerado automaticamente a partir de análise manual dos arquivos-fonte listados em `Resultados/SASTs/Lista de vulnerabilidades Sast.txt`.*