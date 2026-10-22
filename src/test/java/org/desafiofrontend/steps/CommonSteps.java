package org.desafiofrontend.steps;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import org.desafiofrontend.pages.HomePage;
import org.desafiofrontend.pages.SideMenuPage;
import org.desafiofrontend.support.ConfigReader;
import org.desafiofrontend.support.TestContext;

/** Steps de navegação compartilhados por todas as features. */
public class CommonSteps {

    private final TestContext context;

    public CommonSteps(TestContext context) {
        this.context = context;
    }

    @Dado("que acesso o site DemoQA")
    public void queAcessoOSiteDemoQA() {
        new HomePage(context.getDriver()).open(ConfigReader.get("base.url"));
    }

    @E("escolho a opção {string} na página inicial")
    public void escolhoAOpcaoNaPaginaInicial(String opcao) {
        new HomePage(context.getDriver()).selectCard(opcao);
    }

    @E("clico no submenu {string}")
    public void clicoNoSubmenu(String submenu) {
        new SideMenuPage(context.getDriver()).openSubmenu(submenu);
    }
}
