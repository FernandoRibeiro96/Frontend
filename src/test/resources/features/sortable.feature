# language: pt
@regressao
Funcionalidade: Lista ordenável (Sortable)
  Como usuário do site DemoQA
  Quero reordenar os itens da lista com drag and drop
  Para validar o comportamento do componente Sortable

  @sortable
  Cenário: Embaralhar a lista e ordenar os elementos em ordem crescente
    Dado que acesso o site DemoQA
    E escolho a opção "Interactions" na página inicial
    E clico no submenu "Sortable"
    E a página Sortable deve estar carregada
    Quando embaralho a lista usando drag and drop
    Então a lista não deve estar em ordem crescente
    Quando ordeno os elementos da lista em ordem crescente usando drag and drop
    Então os elementos da lista devem estar em ordem crescente
