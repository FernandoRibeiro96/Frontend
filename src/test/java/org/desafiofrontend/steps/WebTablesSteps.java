package org.desafiofrontend.steps;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.desafiofrontend.data.WebTableRecord;
import org.desafiofrontend.data.WebTableRecordFactory;
import org.desafiofrontend.pages.WebTablesPage;
import org.desafiofrontend.support.TestContext;
import org.desafiofrontend.support.WebTableState;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebTablesSteps {

    /** Registros que já existem na tabela ao abrir a página. */
    private static final List<String> INITIAL_EMAILS =
            List.of("cierra@example.com", "alden@example.com", "kierra@example.com");

    private final TestContext context;
    private final WebTableState state;
    private WebTableRecord deletedRecord;

    public WebTablesSteps(TestContext context, WebTableState state) {
        this.context = context;
        this.state = state;
    }

    private WebTablesPage page() {
        return new WebTablesPage(context.getDriver());
    }

    @E("a página Web Tables deve estar carregada")
    public void aPaginaWebTablesDeveEstarCarregada() {
        assertTrue(page().isLoaded(), "A página Web Tables não foi carregada");
    }

    // ---------- Criar ----------
    @Quando("crio um novo registro com valores aleatórios")
    public void crioUmNovoRegistro() {
        createRecord();
    }

    @Quando("crio {int} novos registros com valores aleatórios")
    public void crioNovosRegistros(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            createRecord();
        }
    }

    private void createRecord() {
        WebTableRecord record = WebTableRecordFactory.random();
        page().addRecord(record);
        state.add(record);
    }

    // ---------- Validar ----------
    @Então("o registro deve ser exibido na tabela com os valores informados")
    public void oRegistroDeveSerExibidoNaTabela() {
        assertRecordDisplayed(state.getCurrent());
    }

    @Então("os {int} registros devem ser exibidos na tabela")
    public void osRegistrosDevemSerExibidosNaTabela(int quantidade) {
        assertEquals(quantidade, state.getCreated().size(), "Quantidade de registros criados diferente do esperado");
        state.getCreated().forEach(this::assertRecordDisplayed);
    }

    private void assertRecordDisplayed(WebTableRecord expected) {
        assertEquals(expected.toRowValues(), page().getRecordValues(expected.email()),
                "Valores na tabela diferentes dos informados para " + expected.email());
    }

    // ---------- Editar ----------
    @Quando("edito o novo registro com novos valores")
    public void editoONovoRegistro() {
        WebTableRecord edited = WebTableRecordFactory.random();
        page().editRecord(state.getCurrent().email(), edited);
        state.replaceCurrent(edited);
    }

    @E("os valores antigos do registro não devem mais existir na tabela")
    public void osValoresAntigosNaoDevemMaisExistir() {
        assertTrue(page().waitRecordAbsent(state.getPrevious().email()),
                "O registro com os valores antigos ainda existe na tabela");
    }

    // ---------- Deletar ----------
    @Quando("deleto o registro")
    public void deletoORegistro() {
        WebTableRecord record = state.getCurrent();
        page().deleteRecord(record.email());
        state.remove(record);
    }

    @Quando("deleto todos os novos registros criados")
    public void deletoTodosOsNovosRegistrosCriados() {
        // O DemoQA apaga TODOS os registros criados ao deletar qualquer um deles (bug documentado no README).
        // Por isso só clicamos na lixeira dos que ainda existem e conferimos o resultado final na tabela.
        for (WebTableRecord record : state.getCreated()) {
            if (page().recordExists(record.email())) {
                page().deleteRecord(record.email());
            } else {
                System.out.println("[WebTables] já removido pelo site (bug conhecido): " + record.email());
            }
        }
    }

    @Então("o registro não deve mais existir na tabela")
    public void oRegistroNaoDeveMaisExistirNaTabela() {
        assertTrue(page().waitRecordAbsent(state.getCurrent().email()),
                "O registro ainda existe na tabela");
    }

    @Então("nenhum dos novos registros deve existir na tabela")
    public void nenhumDosNovosRegistrosDeveExistirNaTabela() {
        for (WebTableRecord record : state.getCreated()) {
            assertTrue(page().waitRecordAbsent(record.email()),
                    "O registro ainda existe na tabela: " + record.email());
        }
    }

    // ---------- Bug conhecido: deletar um registro não deveria afetar os demais ----------
    @Quando("deleto apenas o primeiro registro criado")
    public void deletoApenasOPrimeiroRegistroCriado() {
        deletedRecord = state.getCreated().get(0);
        page().deleteRecord(deletedRecord.email());
        state.remove(deletedRecord);
    }

    @Então("o registro deletado não deve mais existir na tabela")
    public void oRegistroDeletadoNaoDeveMaisExistirNaTabela() {
        assertTrue(page().waitRecordAbsent(deletedRecord.email()),
                "O registro deletado ainda existe na tabela: " + deletedRecord.email());
    }

    @E("os demais registros criados devem continuar na tabela")
    public void osDemaisRegistrosCriadosDevemContinuarNaTabela() {
        for (WebTableRecord record : state.getCreated()) {
            assertTrue(page().recordExists(record.email()),
                    "Registro removido indevidamente pelo site: " + record.email());
        }
    }

    @E("os registros iniciais devem permanecer na tabela")
    public void osRegistrosIniciaisDevemPermanecerNaTabela() {
        for (String email : INITIAL_EMAILS) {
            assertTrue(page().recordExists(email), "Registro inicial removido indevidamente: " + email);
        }
    }
}