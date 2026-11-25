# Banco digital DIO com Spring Boot e Spring Security

Projeto didático inspirado em [lab-banco-digital-oo](https://github.com/falvojr/lab-banco-digital-oo). Mantém `Cliente`, `Conta`, `ContaCorrente`, `ContaPoupanca` e `IConta`, agora organizados como aplicação web. Requer JDK 17+ e Maven 3.9+.

## Abrir e executar no VS Code

1. Abrir **a pasta `banco-digital-spring`** 
2. Instale **Extension Pack for Java** e **Spring Boot Extension Pack**, se ainda não instalados.
3. No terminal da pasta, defina duas senhas próprias:

   **PowerShell:**
   ```powershell
   $env:DEMO_VENILTON_PASSWORD = 'troque-esta-senha-1'
   $env:DEMO_MARIA_PASSWORD = 'troque-esta-senha-2'
   mvn spring-boot:run
   ```

   **Linux/macOS:**
   ```bash
   export DEMO_VENILTON_PASSWORD='troque-esta-senha-1'
   export DEMO_MARIA_PASSWORD='troque-esta-senha-2'
   mvn spring-boot:run
   ```

4. Use outro terminal para enviar as requisições. Exemplos no PowerShell (os comandos `curl.exe` também funcionam no Windows):

   ```powershell
   curl.exe -u 'venilton:troque-esta-senha-1' http://localhost:8080/contas
   curl.exe -u 'venilton:troque-esta-senha-1' -H 'Content-Type: application/json' -d '{"valor":100.00}' http://localhost:8080/contas/1/depositos
   curl.exe -u 'venilton:troque-esta-senha-1' -H 'Content-Type: application/json' -d '{"origem":1,"destino":2,"valor":25.00}' http://localhost:8080/transferencias
   curl.exe -u 'venilton:troque-esta-senha-1' http://localhost:8080/contas/2
   ```

## Regras

| Conta | Tipo | Titular / login |
| --- | --- | --- |
| 1 | Corrente | venilton |
| 2 | Poupança | venilton |
| 3 | Corrente | maria |

`GET /contas` lista somente as contas do usuário autenticado. `GET /contas/{numero}` mostra apenas sua própria conta. `POST /contas/{numero}/depositos` e `/saques` recebem `{ "valor": 10.00 }`; `POST /transferencias` recebe `{ "origem": 1, "destino": 3, "valor": 10.00 }`. A origem deve pertencer ao usuário; o destino pode pertencer a outro cliente. A API exige senha e rejeita movimentações com valor não positivo, fração superior a dois dígitos ou saldo insuficiente.

## Organização e limites

- `domain/`: modelo de contas derivado do desafio. Usa `BigDecimal` para valores monetários.
- `service/BancoService`: aplica titularidade e coordena movimentações. A sincronização protege as operações na instância de demonstração.
- `api/`: endpoints, entrada validada e respostas sem dados de autenticação.
- `config/SecurityConfig`: autenticação HTTP Basic, usuários em memória e controle de acesso às rotas.

Os saldos e usuários existem **somente em memória** e são recriados ao reiniciar. Não existe banco de dados, histórico de transações nem isolamento entre várias instâncias. HTTP Basic transmite credenciais em cada chamada: use HTTPS em qualquer ambiente remoto. O CSRF foi desativado somente para o uso didático como API via `curl`/Postman; uma aplicação acessada pelo navegador precisa de estratégia adequada de CSRF. Isto não é um sistema bancário de produção.

Execute os testes com `mvn test` (as senhas dos testes são definidas no próprio contexto de teste).
