package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Menu lateral exibido após escolher um card (Forms > Practice Form, etc.). */
public class SideMenuPage extends BasePage {

    public SideMenuPage(WebDriver driver) {
        super(driver);
    }

    public void openSubmenu(String submenuName) {
        removeAds();
        By item = By.xpath("//div[contains(@class,'element-list')]//span[normalize-space()='" + submenuName + "']");
        click(item);
    }
}
