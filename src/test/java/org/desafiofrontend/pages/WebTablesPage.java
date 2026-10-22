package org.desafiofrontend.pages;

import org.desafiofrontend.data.WebTableRecord;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object de Elements > Web Tables.
 *
 * As linhas são localizadas pelo e-mail (único) percorrendo as páginas da tabela (First/Next).
 * A caixa de busca NÃO é usada: no DemoQA, deletar com texto na busca ativo faz os outros
 * registros criados desaparecerem (bug do site documentado no README).
 */
public class WebTablesPage extends BasePage {

    private static final Duration PAGE_WAIT = Duration.ofSeconds(1);
    private static final Duration ABSENT_WAIT = Duration.ofSeconds(8);
    private static final int MAX_PAGES = 50;

    private static final By ADD_BUTTON = By.id("addNewRecordButton");
    private static final By FIRST_PAGE = By.xpath("//*[self::button or self::a][normalize-space()='First']");
    private static final By NEXT_PAGE = By.xpath("//*[self::button or self::a][normalize-space()='Next']");

    // Formulário (modal) de cadastro/edição
    private static final By FIRST_NAME = By.id("firstName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By EMAIL = By.id("userEmail");
    private static final By AGE = By.id("age");
    private static final By SALARY = By.id("salary");
    private static final By DEPARTMENT = By.id("department");
    private static final By SUBMIT = By.id("submit");
    private static final By MODAL = By.className("modal-content");

    public WebTablesPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        removeAds();
        return waitVisible(ADD_BUTTON).isDisplayed();
    }

    public void addRecord(WebTableRecord record) {
        removeAds();
        click(ADD_BUTTON);
        fillForm(record);
        submitForm();
    }

    public void editRecord(String email, WebTableRecord newData) {
        goToRow(email);
        click(By.xpath(rowXpath(email) + actionXpath("Edit", "edit-record")));
        fillForm(newData);
        submitForm();
    }

    public void deleteRecord(String email) {
        System.out.println("[WebTables] deletando " + email);
        goToRow(email);
        click(By.xpath(rowXpath(email) + actionXpath("Delete", "delete-record")));
        if (!waitRecordAbsent(email)) {
            throw new IllegalStateException("Após deletar, o registro ainda aparece na tabela: "
                    + email + " | " + snapshot());
        }
    }

    /** Valores das 6 primeiras colunas da linha (a 7ª é Action). */
    public List<String> getRecordValues(String email) {
        goToRow(email);
        WebElement row = driver.findElement(rowBy(email));
        return row.findElements(By.xpath("./*")).stream()
                .limit(6)
                .map(cell -> cell.getText().trim())
                .collect(Collectors.toList());
    }

    public boolean recordExists(String email) {
        return findRowAcrossPages(email);
    }

    /** True quando o e-mail não aparece em nenhuma página da tabela. */
    public boolean waitRecordAbsent(String email) {
        try {
            return new WebDriverWait(driver, ABSENT_WAIT)
                    .ignoring(StaleElementReferenceException.class)
                    .until(d -> !findRowAcrossPages(email));
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ---------- paginação ----------
    private void goToRow(String email) {
        if (!findRowAcrossPages(email)) {
            throw new NoSuchElementException("Linha não encontrada em nenhuma página: " + email + " | " + snapshot());
        }
    }

    /** Começa na primeira página e avança com Next até achar a linha. Ao achar, a página fica aberta. */
    private boolean findRowAcrossPages(String email) {
        removeAds();
        clickIfEnabled(FIRST_PAGE);
        WebDriverWait pageWait = new WebDriverWait(driver, PAGE_WAIT);
        for (int page = 0; page < MAX_PAGES; page++) {
            try {
                pageWait.until(ExpectedConditions.visibilityOfElementLocated(rowBy(email)));
                return true;
            } catch (TimeoutException e) {
                if (!clickIfEnabled(NEXT_PAGE)) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean clickIfEnabled(By locator) {
        List<WebElement> found = driver.findElements(locator);
        if (found.isEmpty() || !isNavEnabled(found.get(0))) {
            return false;
        }
        click(locator);
        return true;
    }

    private boolean isNavEnabled(WebElement element) {
        String cssClass = element.getAttribute("class");
        return element.isEnabled()
                && !"true".equals(element.getAttribute("aria-disabled"))
                && (cssClass == null || !cssClass.contains("disabled"));
    }

    /** Linhas visíveis da página atual, para diagnóstico em mensagens de erro. */
    private String snapshot() {
        String rows = driver.findElements(By.xpath("//table//tr | //*[@role='row']")).stream()
                .map(r -> r.getText().replace("\n", " ").trim())
                .filter(text -> !text.isBlank())
                .collect(Collectors.joining(" || "));
        if (rows.isBlank()) {
            String body = driver.findElement(By.tagName("body")).getText().replace("\n", " ");
            rows = body.substring(0, Math.min(body.length(), 800));
        }
        return "linhas visíveis=[" + rows + "]";
    }

    // ---------- formulário ----------
    private void fillForm(WebTableRecord r) {
        replaceText(FIRST_NAME, r.firstName());
        replaceText(LAST_NAME, r.lastName());
        replaceText(EMAIL, r.email());
        replaceText(AGE, String.valueOf(r.age()));
        replaceText(SALARY, String.valueOf(r.salary()));
        replaceText(DEPARTMENT, r.department());
    }

    /** Seleciona todo o conteúdo e digita por cima: funciona em campos vazios ou já preenchidos. */
    private void replaceText(By locator, String text) {
        WebElement input = waitVisible(locator);
        scrollTo(input);
        input.click();
        String selectAll = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.chord(Keys.COMMAND, "a")
                : Keys.chord(Keys.CONTROL, "a");
        input.sendKeys(selectAll);
        input.sendKeys(text);
    }

    private void submitForm() {
        click(SUBMIT);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }

    // ---------- locators ----------
    /** Sobe da célula com o e-mail até a linha (serve para <tr>, role="row" ou rt-tr). */
    private String rowXpath(String email) {
        return "//*[normalize-space(text())='" + email + "']"
                + "/ancestor::*[self::tr or @role='row' "
                + "or contains(concat(' ', normalize-space(@class), ' '), ' rt-tr ')][1]";
    }

    private String actionXpath(String title, String idPrefix) {
        return "//*[@title='" + title + "' or starts-with(@id,'" + idPrefix + "')]";
    }

    private By rowBy(String email) {
        return By.xpath(rowXpath(email));
    }
}