# language: pt
@regressao
Funcionalidade: Janelas do navegador (Browser Windows)
  Como usuário do site DemoQA
  Quero abrir uma nova janela pelo botão New Window
  Para validar o conteúdo exibido e fechá-la em seguida

  @browser_windows
  Cenário: Abrir uma nova janela, validar a mensagem e fechá-la
    Dado que acesso o site DemoQA
    E escolho a opção "Alerts, Frame & Windows" na página inicial
    E clico no submenu "Browser Windows"
    E a página Browser Windows deve estar carregada
    Quando clico no botão New Window
    Então uma nova janela deve ser aberta
    E a nova janela deve exibir a mensagem "This is a sample page"
    Quando fecho a nova janela
    Então devo voltar para a janela original
