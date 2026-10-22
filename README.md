# selenium-projeto

Automação web do **Practice Form** (https://demoqa.com/) com Selenium WebDriver, Java 17, Cucumber (BDD) e Page Object.

## Executar
```bash
mvn clean test
mvn test -Dcucumber.filter.tags="@practice_form"
mvn test -Dbrowser=firefox -Dheadless=true
mvn test "-Dcucumber.filter.tags=@bug_conhecido"
mvn test "-Dcucumber.filter.tags=@browser_windows"
mvn test "-Dcucumber.filter.tags=@web_tables"
mvn test "-Dcucumber.filter.tags=@web_tables and not @bonus"
mvn test "-Dcucumber.filter.tags=@bonus"
mvn test "-Dcucumber.filter.tags=@web_tables_bug"
mvn test "-Dcucumber.filter.tags=@progress_bar"
mvn test "-Dcucumber.filter.tags=@sortable"
```
Relatório: `target/cucumber-reports/report.html`

## Estrutura
- `pages/` Page Objects (locators + ações)
- `steps/` Step definitions (orquestram as pages e fazem as asserções)
- `hooks/` Setup/teardown do driver e screenshot em falha
- `data/` Massa de dados aleatória
- `support/` Config, DriverFactory e TestContext
- `runner/` Runner JUnit Platform
- `resources/features/` Cenários em Gherkin
- `resources/files/` Arquivo .txt usado no upload (versionado no GitHub)

## Bugs:
Título: Botão "Close" do popup de confirmação não fecha o modal

Ambiente: Windows 11, Chrome 155, https://demoqa.com/automation-practice-form
Pré-condição: formulário preenchido e enviado (popup "Thanks for submitting the form" aberto)

Passos:
1. Com o popup aberto, clicar no botão Close

Resultado esperado: o popup é fechado
Resultado atual: o popup permanece aberto

Workarounds: tecla Esc e clique fora do popup fecham normalmente
Botão X (canto superior direito): [testar e preencher]
Frequência: [sempre / intermitente]
Severidade: baixa (há formas alternativas de fechar), mas é uma falha de usabilidade
Evidências: print/vídeo + erros de console (F12), se houver

##############################################################

Título: Deletar um registro com texto na busca ativo apaga os demais registros criados

Ambiente: Windows 11, Chrome 155, https://demoqa.com/webtables

Passos:
1. Criar 3 registros (a@x.com, b@x.com, c@x.com)
2. Digitar "b@x.com" no campo de busca
3. Clicar no ícone de lixeira da linha exibida
4. Apagar o texto da busca

Resultado esperado: permanecem os 3 iniciais + a@x.com e c@x.com
Resultado atual: permanecem apenas os 3 registros iniciais; a@x.com e c@x.com foram removidos

Severidade: alta (perda de dados além do registro deletado)
Workaround: deletar sem usar a busca

###############################################################

Título: Deletar um registro criado apaga todos os demais registros criados

Ambiente: Windows 11, Chrome 155, https://demoqa.com/webtables

Passos:
1. Criar 3 registros (a@x.com, b@x.com, c@x.com)
2. Clicar na lixeira da linha b@x.com (com ou sem texto na busca)

Resultado esperado: permanecem os 3 iniciais + a@x.com e c@x.com
Resultado atual: permanecem apenas os 3 registros iniciais; a@x.com e c@x.com foram removidos

Severidade: alta (perda de dados além do registro deletado)
Workaround: nenhum identificado
Cobertura automatizada: cenário @web_tables_bug