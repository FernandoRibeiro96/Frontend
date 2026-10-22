package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página inicial do DemoQA (cards: Elements, Forms, Alerts...). */
public class HomePage extends BasePage {

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open(String url) {
        driver.get(url);
        removeAds();
    }

    public void selectCard(String cardName) {
        By card = By.xpath("//div[contains(@class,'card-body')]//h5[normalize-space()='" + cardName + "']");
        click(card);
    }
}
