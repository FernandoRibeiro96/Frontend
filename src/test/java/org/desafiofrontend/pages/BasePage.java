package org.desafiofrontend.pages;

import org.desafiofrontend.support.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Ações comuns a todas as páginas. Nenhuma asserção aqui: asserções ficam nos Steps. */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.seconds")));
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        scrollTo(element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException e) {
            // O DemoQA tem banners/anúncios que cobrem elementos: fallback via JS
            jsClick(element);
        }
    }

    protected void type(By locator, String text) {
        WebElement element = waitVisible(locator);
        scrollTo(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText().trim();
    }

    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /** Remove anúncios/rodapé que atrapalham os cliques. */
    protected void removeAds() {
        ((JavascriptExecutor) driver).executeScript(
                "document.querySelectorAll('#fixedban, footer, iframe[id^=google_ads], ins.adsbygoogle, .google-auto-placed').forEach(e => e.remove());");
                //"document.querySelectorAll('#fixedban, footer, iframe[id^=google_ads]').forEach(e => e.remove());");
    }
}
