# language: pt
@regressao
Funcionalidade: Barra de progresso (Progress Bar)
  Como usuário do site DemoQA
  Quero iniciar, parar e resetar a barra de progresso
  Para validar o comportamento dos botões e dos valores

  @progress_bar
  Cenário: Parar a barra antes de 25% e resetá-la ao chegar em 100%
    Dado que acesso o site DemoQA
    E escolho a opção "Widgets" na página inicial
    E clico no submenu "Progress Bar"
    E a página Progress Bar deve estar carregada
    Quando clico no botão Start
    E paro a progress bar antes de atingir 25%
    Então o valor da progress bar deve ser menor ou igual a 25%
    Quando clico no botão Start novamente
    E a progress bar chega a 100%
    E clico no botão Reset
    Então a progress bar deve ser resetada para 0%
