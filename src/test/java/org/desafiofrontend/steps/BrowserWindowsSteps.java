package org.desafiofrontend.steps;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.desafiofrontend.pages.BrowserWindowsPage;
import org.desafiofrontend.pages.SamplePage;
import org.desafiofrontend.support.TestContext;
import org.desafiofrontend.support.WindowManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BrowserWindowsSteps {

    private final TestContext context;
    private final WindowManager windows;

    public BrowserWindowsSteps(TestContext context, WindowManager windows) {
        this.context = context;
        this.windows = windows;
    }

    @E("a página Browser Windows deve estar carregada")
    public void aPaginaBrowserWindowsDeveEstarCarregada() {
        assertTrue(new BrowserWindowsPage(context.getDriver()).isLoaded(),
                "A página Browser Windows não foi carregada");
    }

    @Quando("clico no botão New Window")
    public void clicoNoBotaoNewWindow() {
        windows.rememberCurrentWindow();
        new BrowserWindowsPage(context.getDriver()).clickNewWindow();
    }

    @Então("uma nova janela deve ser aberta")
    public void umaNovaJanelaDeveSerAberta() {
        windows.switchToNewWindow();
        assertEquals(2, windows.windowCount(), "Deveriam existir 2 janelas abertas");
        assertTrue(windows.isOnNewWindow(), "O foco não está na nova janela");
    }

    @E("a nova janela deve exibir a mensagem {string}")
    public void aNovaJanelaDeveExibirAMensagem(String mensagem) {
        assertEquals(mensagem, new SamplePage(context.getDriver()).getHeadingText());
    }

    @Quando("fecho a nova janela")
    public void fechoANovaJanela() {
        windows.closeCurrentAndBackToOriginal();
    }

    @Então("devo voltar para a janela original")
    public void devoVoltarParaAJanelaOriginal() {
        assertEquals(1, windows.windowCount(), "Deveria restar apenas 1 janela");
        assertTrue(new BrowserWindowsPage(context.getDriver()).isLoaded(),
                "A página Browser Windows não está visível na janela original");
    }
}
