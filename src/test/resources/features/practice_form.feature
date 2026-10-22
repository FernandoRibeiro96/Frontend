# language: pt
@regressao
Funcionalidade: Formulário de cadastro de estudante (Practice Form)

  Contexto:
    Dado que acesso o site DemoQA
    E escolho a opção "Forms" na página inicial
    E clico no submenu "Practice Form"
    Quando preencho todo o formulário com valores aleatórios
    E faço upload do arquivo "arquivo-upload.txt"
    E submeto o formulário
    Então um popup de confirmação deve ser exibido
    E o popup deve conter os dados informados

  @practice_form
  Cenário: Submeter o formulário e fechar o popup com ESC
    Quando fecho o popup pressionando ESC
    Então o popup não deve mais ser exibido

  @bug_conhecido
  Cenário: Fechar o popup clicando no botão Close
    Quando fecho o popup clicando no botão Close
    Então o popup não deve mais ser exibido