# language: pt
@regressao
Funcionalidade: Tabela dinâmica (Web Tables)
  Como usuário do site DemoQA
  Quero criar, editar e deletar registros na Web Tables
  Para validar o CRUD da tabela

  Contexto:
    Dado que acesso o site DemoQA
    E escolho a opção "Elements" na página inicial
    E clico no submenu "Web Tables"
    E a página Web Tables deve estar carregada

  @web_tables
  Cenário: Criar, editar e deletar um registro
    Quando crio um novo registro com valores aleatórios
    Então o registro deve ser exibido na tabela com os valores informados
    Quando edito o novo registro com novos valores
    Então o registro deve ser exibido na tabela com os valores informados
    E os valores antigos do registro não devem mais existir na tabela
    Quando deleto o registro
    Então o registro não deve mais existir na tabela

  @web_tables @bonus
  Cenário: Criar 12 novos registros dinamicamente e deletar todos
    Quando crio 12 novos registros com valores aleatórios
    Então os 12 registros devem ser exibidos na tabela
    Quando deleto todos os novos registros criados
    Então nenhum dos novos registros deve existir na tabela
    E os registros iniciais devem permanecer na tabela

  # Bug do site: deletar um registro apaga todos os demais registros criados.
  # Fora da execução padrão (cucumber.filter.tags=not @bug_conhecido). Rodar com -Dcucumber.filter.tags=@web_tables_bug
  @bug_conhecido @web_tables_bug
  Cenário: Deletar um registro não deve apagar os demais registros criados
    Quando crio 3 novos registros com valores aleatórios
    E deleto apenas o primeiro registro criado
    Então o registro deletado não deve mais existir na tabela
    E os demais registros criados devem continuar na tabela
