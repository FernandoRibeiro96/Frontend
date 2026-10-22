package org.desafiofrontend.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.desafiofrontend.data.DataFactory;
import org.desafiofrontend.data.Hobby;
import org.desafiofrontend.data.StudentData;
import org.desafiofrontend.pages.HomePage;
import org.desafiofrontend.pages.PracticeFormPage;
import org.desafiofrontend.pages.SideMenuPage;
import org.desafiofrontend.support.ConfigReader;
import org.desafiofrontend.support.TestContext;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PracticeFormSteps {

    private static final DateTimeFormatter MODAL_DATE = DateTimeFormatter.ofPattern("dd MMMM,yyyy", Locale.ENGLISH);

    private final TestContext context;

    public PracticeFormSteps(TestContext context) {
        this.context = context;
    }

    // Os Page Objects são criados sob demanda, pois o driver só existe depois do @Before.
    private HomePage home() {
        return new HomePage(context.getDriver());
    }

    private SideMenuPage sideMenu() {
        return new SideMenuPage(context.getDriver());
    }

    private PracticeFormPage form() {
        return new PracticeFormPage(context.getDriver());
    }

//    @Dado("que acesso o site DemoQA")
//    public void queAcessoOSiteDemoQA() {
//        home().open(ConfigReader.get("base.url"));
//    }
//
//    @E("escolho a opção {string} na página inicial")
//    public void escolhoAOpcaoNaPaginaInicial(String opcao) {
//        home().selectCard(opcao);
//    }
//
//    @E("clico no submenu {string}")
//    public void clicoNoSubmenu(String submenu) {
//        sideMenu().openSubmenu(submenu);
//        assertTrue(form().isLoaded(), "A página Practice Form não foi carregada");
//    }
    @E("a página Practice Form deve estar carregada")
    public void aPaginaPracticeFormDeveEstarCarregada() {
        assertTrue(form().isLoaded(), "A página Practice Form não foi carregada");
    }

    @Quando("preencho todo o formulário com valores aleatórios")
    public void preenchoTodoOFormularioComValoresAleatorios() {
        StudentData student = DataFactory.randomStudent();
        context.setStudent(student);
        form().fillForm(student);
    }

    @E("faço upload do arquivo {string}")
    public void facoUploadDoArquivo(String nomeArquivo) {
        form().uploadFile(nomeArquivo);
        context.setUploadedFileName(nomeArquivo);
    }

    @E("submeto o formulário")
    public void submetoOFormulario() {
        form().submit();
    }

    @Então("um popup de confirmação deve ser exibido")
    public void umPopupDeConfirmacaoDeveSerExibido() {
        assertTrue(form().isModalDisplayed(), "O popup não foi exibido após o submit");
        assertEquals("Thanks for submitting the form", form().getModalTitle());
    }

    @E("o popup deve conter os dados informados")
    public void oPopupDeveConterOsDadosInformados() {
        StudentData s = context.getStudent();
        PracticeFormPage page = form();

        assertEquals(s.fullName(), page.getModalValue("Student Name"));
        assertEquals(s.email(), page.getModalValue("Student Email"));
        assertEquals(s.gender().getLabel(), page.getModalValue("Gender"));
        assertEquals(s.mobile(), page.getModalValue("Mobile"));
        assertEquals(s.dateOfBirth().format(MODAL_DATE), page.getModalValue("Date of Birth"));
        assertEquals(s.address(), page.getModalValue("Address"));
        assertEquals(s.state() + " " + s.city(), page.getModalValue("State and City"));
        assertEquals(context.getUploadedFileName(), page.getModalValue("Picture"));

        String subjects = page.getModalValue("Subjects");
        s.subjects().forEach(subject ->
                assertTrue(subjects.contains(subject), "Subject ausente no popup: " + subject));

        String hobbies = page.getModalValue("Hobbies");
        for (Hobby hobby : s.hobbies()) {
            assertTrue(hobbies.contains(hobby.getLabel()), "Hobby ausente no popup: " + hobby.getLabel());
        }
    }

//    @Quando("fecho o popup")
//    public void fechoOPopup() {
//        form().closeModal();
//    }
    @Quando("fecho o popup pressionando ESC")
    public void fechoOPopupPressionandoEsc() {
        form().closeModalWithEscape();
    }

    @Quando("fecho o popup clicando no botão Close")
    public void fechoOPopupClicandoNoBotaoClose() {
        form().clickCloseButton();
    }

    @Então("o popup não deve mais ser exibido")
    public void oPopupNaoDeveMaisSerExibido() {
        assertTrue(form().isModalClosed(), "O popup continua aberto");
    }
}
