package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página Alerts, Frame & Windows > Browser Windows. */
public class BrowserWindowsPage extends BasePage {

    private static final By NEW_WINDOW_BUTTON = By.id("windowButton");

    public BrowserWindowsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        removeAds();
        return waitVisible(NEW_WINDOW_BUTTON).isDisplayed();
    }

    public void clickNewWindow() {
        removeAds();
        click(NEW_WINDOW_BUTTON);
    }
}
