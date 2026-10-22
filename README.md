# selenium-projeto

Automação de testes web do site [DemoQA](https://demoqa.com/) com **Selenium WebDriver**, **Java 17**, **Cucumber (BDD)** e **JUnit 5**, organizada com o padrão **Page Object**.

O projeto cobre cinco fluxos do desafio (Practice Form, Browser Windows, Web Tables, Progress Bar e Sortable), inclui os pontos adicionais da Web Tables e **registra dois bugs reais do site**, cada um com um cenário automatizado que o reproduz.

---

## Sumário

1. [Tecnologias](#tecnologias)
2. [Testes automatizados](#testes-automatizados)
3. [Como o Cucumber (BDD) foi usado](#como-o-cucumber-bdd-foi-usado)
4. [Padrões de projeto e arquitetura](#padrões-de-projeto-e-arquitetura)
5. [Estrutura do projeto](#estrutura-do-projeto)
6. [Decisões técnicas](#decisões-técnicas)
7. [Como executar](#como-executar)
8. [Bugs encontrados](#bugs-encontrados)
9. [Observações](#observações)
10. [Melhorias futuras](#melhorias-futuras)

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem |
| Maven | 3.9+ | Build e gerenciamento de dependências |
| Selenium WebDriver | 4.25.0 | Automação do navegador |
| Cucumber (Java + JUnit Platform Engine) | 7.20.1 | BDD com Gherkin em português |
| Cucumber PicoContainer | 7.20.1 | Injeção de dependência entre Steps e Hooks |
| JUnit 5 (Jupiter + Platform Suite) | 5.11.0 | Execução e asserções |
| Maven Surefire | 3.5.0 | Execução dos testes |

Não é necessário instalar o chromedriver: o **Selenium Manager** (incluído no Selenium 4.6+) baixa o driver compatível automaticamente.

---

## Testes automatizados

| # | Funcionalidade | Feature | Tag | O que é validado |
|---|---|---|---|---|
| 1 | **Practice Form** | `practice_form.feature` | `@practice_form` | Preenche todo o formulário com valores aleatórios, faz upload de um arquivo `.txt`, submete, confirma que o popup abriu **e que cada dado exibido confere com o informado**, fecha o popup e confirma que ele sumiu |
| 2 | **Browser Windows** | `browser_windows.feature` | `@browser_windows` | Clica em *New Window*, confirma que uma nova janela abriu, valida a mensagem *"This is a sample page"*, fecha a nova janela e volta para a original |
| 3 | **Web Tables** (CRUD) | `web_tables.feature` | `@web_tables` | Cria um registro, valida os valores na tabela, edita, valida a edição (e que os valores antigos sumiram) e deleta |
| 4 | **Web Tables** (bônus) | `web_tables.feature` | `@web_tables @bonus` | Cria **12 registros de forma dinâmica** pelo Cucumber, valida todos e deleta todos os novos registros |
| 5 | **Progress Bar** | `progress_bar.feature` | `@progress_bar` | Inicia a barra, **para antes de 25%**, valida que o valor é ≤ 25%, reinicia, espera 100% e executa o *Reset* |
| 6 | **Sortable** | `sortable.feature` | `@sortable` | Embaralha a lista e a coloca em **ordem crescente** usando drag and drop |
| 7 | Bug: botão *Close* | `practice_form.feature` | `@bug_conhecido` | Reproduz o defeito do botão *Close* do popup (ver [Bugs encontrados](#bugs-encontrados)) |
| 8 | Bug: deleção na Web Tables | `web_tables.feature` | `@bug_conhecido @web_tables_bug` | Reproduz o defeito de deleção que apaga registros que não deveriam ser removidos |

Todos os cenários também recebem a tag `@regressao` (definida no nível da funcionalidade).

---

## Como o Cucumber (BDD) foi usado

Os cenários são escritos em **Gherkin em português** (`# language: pt`), em linguagem de negócio, sem nenhum detalhe técnico (sem seletores, sem esperas):

```gherkin
# language: pt
@regressao
Funcionalidade: Janelas do navegador (Browser Windows)

  @browser_windows
  Cenário: Abrir uma nova janela, validar a mensagem e fechá-la
    Dado que acesso o site DemoQA
    E escolho a opção "Alerts, Frame & Windows" na página inicial
    E clico no submenu "Browser Windows"
    Quando clico no botão New Window
    Então uma nova janela deve ser aberta
    E a nova janela deve exibir a mensagem "This is a sample page"
    Quando fecho a nova janela
    Então devo voltar para a janela original
```

Recursos do Cucumber aplicados:

- **Steps reutilizáveis:** os passos de navegação (`acesso o site`, `escolho a opção`, `clico no submenu`) ficam em `CommonSteps` e são usados por todas as features.
- **Parâmetros nos steps:** `{string}` e `{int}`. O bônus da Web Tables usa `crio {int} novos registros com valores aleatórios`, então a quantidade vem do Gherkin e cada registro é gerado em tempo de execução.
- **`Contexto` (Background):** a Web Tables compartilha a navegação entre os cenários sem repetir passos.
- **Texto opcional:** `clico no botão Start( novamente)` atende duas frases com um único método.
- **Tags:** permitem rodar o que interessa (`@practice_form`, `@sortable`...) e **separar os cenários de bug conhecido** da execução padrão.
- **Hooks (`@Before` / `@After`):** abrem e fecham o navegador a cada cenário e anexam um **screenshot ao relatório quando o cenário falha**.
- **Relatórios:** HTML e JSON gerados automaticamente em `target/cucumber-reports/`.
- **Runner moderno:** `RunCucumberTest` usa a JUnit Platform Suite (`@Suite` + `@IncludeEngines("cucumber")`). Glue e plugins ficam no `junit-platform.properties`.

---

## Padrões de projeto e arquitetura

A responsabilidade de cada camada é bem delimitada:

```
Feature (Gherkin)  →  Steps  →  Pages (Page Object)  →  WebDriver
   "o quê"          orquestra    "como" interagir        navegador
                    e valida     com cada tela
```

| Camada | Pasta | Responsabilidade | O que **não** faz |
|---|---|---|---|
| Features | `resources/features` | Descrever o comportamento em linguagem de negócio | Não conhece Selenium |
| Steps | `steps` | Traduzir o Gherkin em chamadas às pages e fazer as **asserções** | Não tem seletores |
| Pages | `pages` | Guardar **seletores e ações** de cada tela | Não faz asserções |
| Data | `data` | Gerar massa de dados aleatória | — |
| Support | `support` | Configuração, criação do driver e estado do cenário | — |
| Hooks | `hooks` | Ciclo de vida do navegador e evidência de falha | — |

### Padrões e técnicas aplicados

- **Page Object Model:** uma classe por tela (`PracticeFormPage`, `WebTablesPage`, `SortablePage`...). Se o HTML mudar, só a page é alterada.
- **Herança com `BasePage`:** centraliza esperas explícitas, scroll, clique com *fallback* em JavaScript e remoção de anúncios. As pages só implementam o que é específico delas.
- **Injeção de dependência (PicoContainer):** o `TestContext` (driver e dados do cenário) e o `WindowManager` são injetados nos construtores de Steps e Hooks, com **uma instância por cenário**, sem variáveis estáticas. Isso mantém os cenários isolados e deixa a execução paralela possível no futuro.
- **Factory:** `DriverFactory` cria o navegador (Chrome, Firefox ou Edge) conforme a configuração. `DataFactory` e `WebTableRecordFactory` geram os dados aleatórios.
- **Objetos de valor imutáveis:** `StudentData` e `WebTableRecord` são `record` do Java 17.
- **Configuração externa:** `config.properties` com possibilidade de sobrescrever qualquer chave por linha de comando (`-Dbrowser=firefox`).
- **Separação entre ação e verificação:** as pages devolvem estados e valores. As asserções ficam nos steps.

---

## Estrutura do projeto

```
selenium-projeto/
├── pom.xml
├── README.md
└── src/test/
    ├── java/org/desafiofrontend/
    │   ├── data/
    │   │   ├── DataFactory.java            # massa aleatória do Practice Form
    │   │   ├── StudentData.java
    │   │   ├── Gender.java
    │   │   ├── Hobby.java
    │   │   ├── WebTableRecord.java
    │   │   └── WebTableRecordFactory.java  # registros únicos (e-mail único)
    │   ├── hooks/
    │   │   └── Hooks.java                  # abre/fecha driver, screenshot em falha
    │   ├── pages/
    │   │   ├── BasePage.java
    │   │   ├── HomePage.java
    │   │   ├── SideMenuPage.java
    │   │   ├── PracticeFormPage.java
    │   │   ├── BrowserWindowsPage.java
    │   │   ├── SamplePage.java
    │   │   ├── WebTablesPage.java
    │   │   ├── ProgressBarPage.java
    │   │   └── SortablePage.java
    │   ├── runner/
    │   │   └── RunCucumberTest.java
    │   ├── steps/
    │   │   ├── CommonSteps.java            # navegação compartilhada
    │   │   ├── PracticeFormSteps.java
    │   │   ├── BrowserWindowsSteps.java
    │   │   ├── WebTablesSteps.java
    │   │   ├── ProgressBarSteps.java
    │   │   └── SortableSteps.java
    │   └── support/
    │       ├── ConfigReader.java
    │       ├── DriverFactory.java
    │       ├── TestContext.java            # estado compartilhado do cenário
    │       ├── WindowManager.java          # controle de janelas/abas
    │       └── WebTableState.java          # registros criados no cenário
    └── resources/
        ├── features/
        │   ├── practice_form.feature
        │   ├── browser_windows.feature
        │   ├── web_tables.feature
        │   ├── progress_bar.feature
        │   └── sortable.feature
        ├── files/
        │   └── arquivo-upload.txt          # arquivo usado no upload do Practice Form
        ├── config.properties
        └── junit-platform.properties
```

O arquivo de upload fica versionado em `src/test/resources/files/`, como pede o desafio. O teste o localiza pelo classpath, então funciona em qualquer máquina e em CI.

---

## Decisões técnicas

| Problema | Decisão |
|---|---|
| **Instabilidade por tempo** | Somente **esperas explícitas** (`WebDriverWait`). Nenhum `Thread.sleep` e nenhuma espera implícita |
| **Anúncios do DemoQA cobrem elementos** | `BasePage.click()` rola até o elemento e, se o clique for interceptado, usa *fallback* em JavaScript. Os banners e o rodapé são removidos da página |
| **Dados fixos geram testes frágeis e repetitivos** | Todos os dados são aleatórios a cada execução. Na Web Tables o e-mail é único e serve de chave para localizar a linha |
| **Validar de verdade, não só "abriu o popup"** | O popup do Practice Form é conferido campo a campo contra o que foi preenchido (nome, e-mail, gênero, celular, data, subjects, hobbies, endereço, estado/cidade e nome do arquivo) |
| **Tabela paginada (10 linhas por página)** | A linha é localizada percorrendo as páginas (*First / Next*), então o teste funciona com qualquer quantidade de registros |
| **Barra de progresso avança ~1% a cada 100 ms** | "Parar antes de 25%" via Selenium seria instável. A verificação do valor e o clique em *Stop* rodam **dentro do navegador**, no mesmo instante, com margem de segurança (Stop em 20% para o limite de 25%) |
| **Barra em 0% tem largura zero** | O Selenium a considera invisível. A página carregada é confirmada pelo botão e o valor é lido de `aria-valuenow` |
| **Drag and drop com `dragAndDrop()` do Selenium costuma falhar** | O arraste usa ações de mouse separadas (pressionar, mover, soltar) com pequenas pausas e até 3 tentativas por item. A ordenação é por seleção, e todo arraste é para cima |
| **A lista Sortable já carrega ordenada** | O teste **embaralha antes** de ordenar. Sem isso o teste passaria sem arrastar nada |
| **Janelas múltiplas** | `WindowManager` guarda a janela original, espera a segunda abrir, troca o foco e devolve o foco ao fechar |
| **Bugs do site não devem esconder-se nem quebrar a suíte** | Cenários com `@bug_conhecido` ficam fora da execução padrão, mas continuam no repositório como evidência executável |

---

## Como executar

### Pré-requisitos

- JDK 17
- Maven 3.9+
- Google Chrome instalado (ou Firefox/Edge, ver abaixo)
- Acesso à internet (o site é externo e o Selenium Manager baixa o driver)

### Comandos

```bash
# Suíte completa (exclui os cenários @bug_conhecido por padrão)
mvn clean test

# Um teste específico
mvn test "-Dcucumber.filter.tags=@practice_form"
mvn test "-Dcucumber.filter.tags=@browser_windows"
mvn test "-Dcucumber.filter.tags=@web_tables"
mvn test "-Dcucumber.filter.tags=@bonus"
mvn test "-Dcucumber.filter.tags=@progress_bar"
mvn test "-Dcucumber.filter.tags=@sortable"

# Reproduzir os bugs conhecidos (estes cenários FALHAM enquanto o defeito existir)
mvn test "-Dcucumber.filter.tags=@bug_conhecido"

# Todos os testes estáveis, de forma explícita
mvn test "-Dcucumber.filter.tags=@regressao and not @bug_conhecido"

# Outro navegador e/ou sem interface gráfica
mvn test "-Dbrowser=firefox" "-Dheadless=true"
```

> **PowerShell (Windows):** coloque o argumento `-D...` inteiro entre aspas, como nos exemplos. Sem as aspas, o PowerShell divide o argumento no ponto e o Maven falha com `Unknown lifecycle phase`.

> O filtro padrão `not @bug_conhecido` está no `junit-platform.properties`. Ao passar `-Dcucumber.filter.tags`, ele é substituído pelo valor informado.

### Configuração (`config.properties`)

| Chave | Padrão | Descrição |
|---|---|---|
| `base.url` | `https://demoqa.com/` | URL base |
| `browser` | `chrome` | `chrome`, `firefox` ou `edge` |
| `headless` | `false` | Executa sem abrir a janela do navegador |
| `timeout.seconds` | `15` | Tempo máximo das esperas explícitas |

Qualquer chave pode ser sobrescrita pela linha de comando (`-Dtimeout.seconds=30`).

### Relatórios e evidências

Após a execução:

- `target/cucumber-reports/report.html`: relatório visual, com **screenshot anexado nos cenários que falharam**
- `target/cucumber-reports/report.json`: para integração com outras ferramentas

---

## Bugs encontrados

Durante a automação foram identificados dois comportamentos incorretos no site. Ambos foram **reproduzidos manualmente** antes de serem tratados como bugs, e cada um tem um cenário automatizado com a tag `@bug_conhecido`.

### Bug 1: o botão *Close* do popup não fecha o modal

| Campo | Detalhe |
|---|---|
| **Onde** | https://demoqa.com/automation-practice-form |
| **Ambiente** | Windows 11, Chrome 155 |
| **Severidade** | Baixa (há formas alternativas de fechar o popup) |
| **Pré-condição** | Formulário preenchido e enviado (popup *Thanks for submitting the form* aberto) |

**Passos para reproduzir**

1. Acessar *Forms > Practice Form*
2. Preencher o formulário e clicar em *Submit*
3. Com o popup aberto, clicar no botão **Close**

**Resultado esperado:** o popup é fechado.
**Resultado atual:** o popup permanece aberto.
**Workaround:** a tecla **Esc** e o clique **fora** do popup o fecham normalmente.

**Tratamento na automação:** o cenário principal (`@practice_form`) fecha o popup com **Esc** e valida que ele sumiu. O cenário `@bug_conhecido` clica em *Close* e falha enquanto o defeito existir.

### Bug 2: deletar um registro da Web Tables apaga todos os demais registros criados

| Campo | Detalhe |
|---|---|
| **Onde** | https://demoqa.com/webtables |
| **Ambiente** | Windows 11, Chrome 155 |
| **Severidade** | Alta (perda de dados além do registro deletado) |

**Passos para reproduzir**

1. Acessar *Elements > Web Tables*
2. Criar 3 registros (por exemplo `a@x.com`, `b@x.com`, `c@x.com`)
3. Clicar no ícone da lixeira da linha `b@x.com` (o resultado é o mesmo com ou sem texto na busca)

**Resultado esperado:** permanecem os 3 registros iniciais e também `a@x.com` e `c@x.com`.
**Resultado atual:** permanecem **apenas os 3 registros iniciais**. `a@x.com` e `c@x.com` também foram removidos.
**Workaround:** nenhum identificado.

**Tratamento na automação:**

- O cenário `@bonus` ("criar 12 registros e deletar todos") continua válido. O passo de deleção clica na lixeira apenas dos registros que ainda existem e, no fim, **confere na tabela** que nenhum dos 12 sobrou e que os 3 iniciais permanecem.
- O cenário `@bug_conhecido @web_tables_bug` cria 3 registros, deleta só o primeiro e exige que os outros dois continuem na tabela. Ele **falha enquanto o defeito existir**, com a mensagem `Registro removido indevidamente pelo site`.

---

## Observações

- **Site externo:** o DemoQA é um site de prática e pode apresentar lentidão, anúncios e instabilidades que não dependem da automação.
- **Aviso de CDP no console** (`Unable to find CDP implementation matching ...`): aparece quando o Chrome é mais novo que o suporte a DevTools do Selenium 4.25.0. É informativo, porque o projeto não usa recursos de DevTools. Atualizar o `selenium-java` elimina o aviso.
- **Caracteres acentuados quebrados no console do Windows:** é só a codificação do terminal (por exemplo `CenÃ¡rio`). Os testes e o relatório HTML não são afetados. Para corrigir no terminal, rode `chcp 65001` antes do Maven.

## Como adicionar um novo teste

1. Criar `nova_funcionalidade.feature` em `resources/features`, em Gherkin.
2. Criar a page em `pages/` estendendo `BasePage` (seletores e ações, sem asserções).
3. Criar a classe de steps em `steps/`, recebendo o `TestContext` no construtor e fazendo as asserções.
4. Reaproveitar os steps de navegação do `CommonSteps`.

Nenhuma outra configuração é necessária: o Cucumber descobre features e steps automaticamente.

## Melhorias futuras

- Execução em paralelo (driver por thread) e Selenium Grid/Docker
- Relatório Allure com histórico e anexos
- Pipeline de CI (GitHub Actions) executando em modo headless e publicando o relatório
- Logs estruturados (SLF4J) e *seed* para reproduzir dados aleatórios
- Asserções com AssertJ (*soft assertions*) para listar todas as divergências de uma vez
- Cenários negativos e data-driven (`Esquema do Cenário`) nos formulários
- Componentes reutilizáveis (seletor de data, *react-select*, modal)