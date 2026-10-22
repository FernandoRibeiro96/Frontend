package org.desafiofrontend.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.desafiofrontend.support.DriverFactory;
import org.desafiofrontend.support.TestContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp() {
        context.setDriver(DriverFactory.create());
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = context.getDriver();
        if (driver == null) {
            return;
        }
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Screenshot da falha");
        }
        driver.quit();
    }
}
