package org.desafiofrontend.support;

import org.desafiofrontend.data.StudentData;
import org.openqa.selenium.WebDriver;

/**
 * Estado compartilhado entre Hooks e Steps de um mesmo cenário.
 * O PicoContainer cria uma instância por cenário e injeta nos construtores.
 */
public class TestContext {

    private WebDriver driver;
    private StudentData student;
    private String uploadedFileName;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public StudentData getStudent() {
        return student;
    }

    public void setStudent(StudentData student) {
        this.student = student;
    }

    public String getUploadedFileName() {
        return uploadedFileName;
    }

    public void setUploadedFileName(String uploadedFileName) {
        this.uploadedFileName = uploadedFileName;
    }
}
