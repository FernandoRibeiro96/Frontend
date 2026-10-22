package org.desafiofrontend.steps;

import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.desafiofrontend.pages.SortablePage;
import org.desafiofrontend.support.TestContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SortableSteps {

    private static final List<String> ASCENDING = List.of("One", "Two", "Three", "Four", "Five", "Six");
    /** Mesma ordem desordenada mostrada no enunciado do desafio. */
    private static final List<String> SCRAMBLED = List.of("Six", "Two", "One", "Four", "Five", "Three");

    private final TestContext context;

    public SortableSteps(TestContext context) {
        this.context = context;
    }

    private SortablePage page() {
        return new SortablePage(context.getDriver());
    }

    @E("a página Sortable deve estar carregada")
    public void aPaginaSortableDeveEstarCarregada() {
        assertTrue(page().isLoaded(), "A página Sortable não foi carregada");
    }

    @Quando("embaralho a lista usando drag and drop")
    public void embaralhoAListaUsandoDragAndDrop() {
        // O DemoQA carrega a lista já ordenada (One..Six); sem embaralhar antes, o teste passaria sem arrastar nada.
        page().arrangeTo(SCRAMBLED);
    }

    @Então("a lista não deve estar em ordem crescente")
    public void aListaNaoDeveEstarEmOrdemCrescente() {
        assertNotEquals(ASCENDING, page().getItems(), "O embaralhamento não alterou a ordem da lista");
    }

    @Quando("ordeno os elementos da lista em ordem crescente usando drag and drop")
    public void ordenoOsElementosDaListaEmOrdemCrescente() {
        page().sortAscending();
    }

    @Então("os elementos da lista devem estar em ordem crescente")
    public void osElementosDaListaDevemEstarEmOrdemCrescente() {
        assertEquals(ASCENDING, page().getItems());
    }
}