package org.desafiofrontend.pages;

import org.desafiofrontend.data.Hobby;
import org.desafiofrontend.data.StudentData;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Page Object do Practice Form + popup de confirmação. */
public class PracticeFormPage extends BasePage {

    // ---------- Formulário ----------
    private static final By TITLE = By.xpath("//h1[normalize-space()='Practice Form']");
    private static final By FIRST_NAME = By.id("firstName");
    private static final By LAST_NAME = By.id("lastName");
    private static final By EMAIL = By.id("userEmail");
    private static final By MOBILE = By.id("userNumber");
    private static final By DATE_OF_BIRTH = By.id("dateOfBirthInput");
    private static final By SUBJECTS = By.id("subjectsInput");
    private static final By UPLOAD = By.id("uploadPicture");
    private static final By ADDRESS = By.id("currentAddress");
    private static final By STATE = By.id("state");
    private static final By CITY = By.id("city");
    private static final By SUBMIT = By.id("submit");

    // ---------- Popup ----------
    private static final By MODAL = By.className("modal-content");
    private static final By MODAL_TITLE = By.id("example-modal-sizes-title-lg");
    private static final By MODAL_CLOSE = By.id("closeLargeModal");

    private static final DateTimeFormatter INPUT_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);

    public PracticeFormPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        removeAds();
        return waitVisible(TITLE).isDisplayed();
    }

    // ---------- Preenchimento ----------
    public void fillForm(StudentData s) {
        type(FIRST_NAME, s.firstName());
        type(LAST_NAME, s.lastName());
        type(EMAIL, s.email());
        selectGender(s);
        type(MOBILE, s.mobile());
        fillDateOfBirth(s);
        fillSubjects(s);
        selectHobbies(s);
        type(ADDRESS, s.address());
        selectState(s.state());
        selectCity(s.city());
    }

    private void selectGender(StudentData s) {
        click(By.cssSelector("label[for='gender-radio-" + s.gender().getIndex() + "']"));
    }

    private void fillDateOfBirth(StudentData s) {
        WebElement input = waitVisible(DATE_OF_BIRTH);
        String selectAllKey = System.getProperty("os.name").toLowerCase().contains("mac")
                ? Keys.chord(Keys.COMMAND, "a")
                : Keys.chord(Keys.CONTROL, "a");
        input.click();
        input.sendKeys(selectAllKey);
        input.sendKeys(s.dateOfBirth().format(INPUT_DATE));
        input.sendKeys(Keys.ENTER);
    }

    private void fillSubjects(StudentData s) {
        for (String subject : s.subjects()) {
            WebElement input = waitVisible(SUBJECTS);
            input.sendKeys(subject);
            input.sendKeys(Keys.ENTER);
        }
    }

    private void selectHobbies(StudentData s) {
        for (Hobby hobby : s.hobbies()) {
            click(By.cssSelector("label[for='hobbies-checkbox-" + hobby.getIndex() + "']"));
        }
    }

    private void selectState(String state) {
        click(STATE);
        click(dropdownOption(state));
    }

    private void selectCity(String city) {
        wait.until(ExpectedConditions.elementToBeClickable(CITY));
        click(CITY);
        click(dropdownOption(city));
    }

    private By dropdownOption(String text) {
        return By.xpath("//div[starts-with(@id,'react-select') and contains(@id,'-option-') "
                + "and normalize-space()='" + text + "']");
    }

    // ---------- Upload ----------
    public void uploadFile(String fileName) {
        try {
            URL resource = getClass().getClassLoader().getResource("files/" + fileName);
            if (resource == null) {
                throw new IllegalArgumentException("Arquivo não encontrado em src/test/resources/files: " + fileName);
            }
            Path path = Paths.get(resource.toURI());
            // input type=file fica "escondido" visualmente, mas aceita sendKeys com o caminho absoluto
            wait.until(ExpectedConditions.presenceOfElementLocated(UPLOAD)).sendKeys(path.toAbsolutePath().toString());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Caminho inválido para o arquivo " + fileName, e);
        }
    }

    // ---------- Submit ----------
    public void submit() {
        click(SUBMIT);
    }

    // ---------- Popup ----------
    public boolean isModalDisplayed() {
        return waitVisible(MODAL).isDisplayed();
    }

    public String getModalTitle() {
        return getText(MODAL_TITLE);
    }

    /** Retorna o valor da linha da tabela do popup (ex.: "Student Name", "Gender"). */
    public String getModalValue(String label) {
        By value = By.xpath("//div[contains(@class,'modal-body')]//td[normalize-space()='" + label
                + "']/following-sibling::td");
        return getText(value);
    }

    public void closeModalWithEscape() {
        new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    }

    public void clickCloseButton() {
        click(MODAL_CLOSE);
    }

    public boolean isModalClosed() {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL));
    }
}
