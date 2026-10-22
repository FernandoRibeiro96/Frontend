package org.desafiofrontend.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Controla janelas/abas do navegador. O PicoContainer cria uma instância por cenário,
 * então o handle da janela original fica guardado sem precisar de estático.
 */
public class WindowManager {

    private final TestContext context;
    private String originalHandle;

    public WindowManager(TestContext context) {
        this.context = context;
    }

    private WebDriver driver() {
        return context.getDriver();
    }

    /** Guarda a janela atual para voltar a ela depois. */
    public void rememberCurrentWindow() {
        originalHandle = driver().getWindowHandle();
    }

    public int windowCount() {
        return driver().getWindowHandles().size();
    }

    /** Espera a segunda janela existir e passa o foco do driver para ela. */
    public void switchToNewWindow() {
        new WebDriverWait(driver(), Duration.ofSeconds(ConfigReader.getInt("timeout.seconds")))
                .until(ExpectedConditions.numberOfWindowsToBe(2));

        String newHandle = driver().getWindowHandles().stream()
                .filter(handle -> !handle.equals(originalHandle))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma nova janela foi encontrada"));

        driver().switchTo().window(newHandle);
    }

    public boolean isOnNewWindow() {
        return !driver().getWindowHandle().equals(originalHandle);
    }

    /** Fecha a janela em foco e devolve o foco para a janela original. */
    public void closeCurrentAndBackToOriginal() {
        driver().close();
        driver().switchTo().window(originalHandle);
    }
}
