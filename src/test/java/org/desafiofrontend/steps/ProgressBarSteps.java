package org.desafiofrontend.steps;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.desafiofrontend.pages.ProgressBarPage;
import org.desafiofrontend.support.TestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProgressBarSteps {

    /** Margem de segurança: para o limite de 25% o Stop é acionado em 20%. */
    private static final int STOP_MARGIN = 5;

    private final TestContext context;

    public ProgressBarSteps(TestContext context) {
        this.context = context;
    }

    private ProgressBarPage page() {
        return new ProgressBarPage(context.getDriver());
    }

    @E("a página Progress Bar deve estar carregada")
    public void aPaginaProgressBarDeveEstarCarregada() {
        assertTrue(page().isLoaded(), "A página Progress Bar não foi carregada");
    }

    @Quando("clico no botão Start( novamente)")
    public void clicoNoBotaoStart() {
        page().clickStartStop();
    }

    @E("paro a progress bar antes de atingir {int}%")
    public void paroAProgressBarAntesDeAtingir(int limite) {
        int alvo = Math.max(1, limite - STOP_MARGIN);
        int valorNoStop = page().stopWhenReaches(alvo);
        assertTrue(valorNoStop >= 0, "A progress bar não chegou a " + alvo + "% no tempo esperado");
    }

    @Então("o valor da progress bar deve ser menor ou igual a {int}%")
    public void oValorDaProgressBarDeveSerMenorOuIgualA(int limite) {
        int valor = page().getValue();
        assertTrue(valor > 0, "A progress bar não iniciou (valor " + valor + "%)");
        assertTrue(valor <= limite, "A progress bar passou de " + limite + "%: valor atual " + valor + "%");
    }

    @E("a progress bar chega a 100%")
    public void aProgressBarChegaA100() {
        assertTrue(page().waitUntilFull(), "A progress bar não chegou a 100% no tempo esperado");
    }

    @E("clico no botão Reset")
    public void clicoNoBotaoReset() {
        page().clickReset();
    }

    @Então("a progress bar deve ser resetada para 0%")
    public void aProgressBarDeveSerResetadaPara0() {
        assertTrue(page().waitUntilEmpty(), "A progress bar não voltou para 0% após o Reset");
        assertEquals("0%", page().getText());
        assertTrue(page().isStartButtonDisplayed(), "O botão Start deveria estar disponível após o Reset");
    }
}
