package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Page Object de Widgets > Progress Bar. */
public class ProgressBarPage extends BasePage {

    private static final By BAR = By.cssSelector("[role='progressbar']");
    private static final By START_STOP_BUTTON = By.id("startStopButton");
    private static final By RESET_BUTTON = By.id("resetButton");

    private static final Duration FULL_PROGRESS_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration RESET_TIMEOUT = Duration.ofSeconds(5);

    /**
     * A barra avança ~1% a cada 100 ms. Um clique no Stop vindo do Selenium leva dezenas de
     * milissegundos de ida e volta, o que torna "parar antes de X%" instável. Por isso a
     * checagem do valor e o clique no Stop rodam dentro do navegador, no mesmo instante.
     */
    private static final String STOP_AT_SCRIPT = """
            var target = arguments[0];
            var done = arguments[arguments.length - 1];
            var bar = document.querySelector("[role='progressbar']");
            var button = document.getElementById('startStopButton');
            var guard = setTimeout(function () { clearInterval(timer); done(-1); }, 20000);
            var timer = setInterval(function () {
                var value = parseInt(bar.getAttribute('aria-valuenow') || bar.textContent, 10);
                if (value >= target) {
                    clearInterval(timer);
                    clearTimeout(guard);
                    button.click();
                    done(value);
                }
            }, 5);
            """;

    public ProgressBarPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        removeAds();
        // Com 0% a barra tem largura zero e o Selenium a considera "invisível":
        // a página carregada é confirmada pelo botão, e a barra só precisa existir no DOM.
        return waitVisible(START_STOP_BUTTON).isDisplayed() && findBar() != null;
    }

    private WebElement findBar() {
        return wait.until(ExpectedConditions.presenceOfElementLocated(BAR));
    }

    /** O mesmo botão alterna entre Start e Stop. */
    public void clickStartStop() {
        removeAds();
        click(START_STOP_BUTTON);
    }

    /** Valor atual da barra em % (lê aria-valuenow; se não houver, o texto "NN%"). */
    public int getValue() {
        WebElement bar = findBar();
        String value = bar.getAttribute("aria-valuenow");
        if (value == null || value.isBlank()) {
            value = bar.getText().replace("%", "").trim();
        }
        return Integer.parseInt(value);
    }

    /** Texto da barra (ex.: "33%"). Usa textContent porque getText() devolve vazio quando a barra está com 0%. */
    public String getText() {
        String text = findBar().getDomProperty("textContent");
        return text == null ? "" : text.trim();
    }

    /** Clica em Stop assim que a barra atingir o valor. Retorna o valor no clique, ou -1 se não chegou a tempo. */
    public int stopWhenReaches(int target) {
        Object result = ((JavascriptExecutor) driver).executeAsyncScript(STOP_AT_SCRIPT, target);
        return ((Number) result).intValue();
    }

    public boolean waitUntilFull() {
        return new WebDriverWait(driver, FULL_PROGRESS_TIMEOUT).until(d -> getValue() == 100);
    }

    public void clickReset() {
        click(RESET_BUTTON);
    }

    public boolean waitUntilEmpty() {
        return new WebDriverWait(driver, RESET_TIMEOUT).until(d -> getValue() == 0);
    }

    public boolean isStartButtonDisplayed() {
        return waitVisible(START_STOP_BUTTON).isDisplayed();
    }
}