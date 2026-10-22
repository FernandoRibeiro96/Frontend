package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página https://demoqa.com/sample, aberta na nova janela. */
public class SamplePage extends BasePage {

    private static final By HEADING = By.id("sampleHeading");

    public SamplePage(WebDriver driver) {
        super(driver);
    }

    public String getHeadingText() {
        return getText(HEADING);
    }
}
