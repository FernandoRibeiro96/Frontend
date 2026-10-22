package org.desafiofrontend.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Page Object de Interactions > Sortable (aba List). */
public class SortablePage extends BasePage {

    private static final By LIST_TAB = By.id("demo-tab-list");
    private static final By LIST_ITEMS = By.cssSelector("#demo-tabpane-list .list-group-item");

    private static final int MAX_DRAG_ATTEMPTS = 3;
    private static final Duration REORDER_WAIT = Duration.ofSeconds(2);

    /** Os itens são números por extenso; esta tabela define o que é "ordem crescente". */
    private static final Map<String, Integer> WORD_VALUE = Map.of(
            "one", 1, "two", 2, "three", 3, "four", 4, "five", 5,
            "six", 6, "seven", 7, "eight", 8, "nine", 9, "ten", 10);

    public SortablePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        removeAds();
        click(LIST_TAB);
        return waitVisible(LIST_ITEMS).isDisplayed();
    }

    /** Textos dos itens na ordem em que aparecem na tela. */
    public List<String> getItems() {
        return driver.findElements(LIST_ITEMS).stream()
                .map(item -> item.getText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Ordena por seleção: para cada posição, arrasta para ela o item que deveria estar ali.
     * Como as posições anteriores já estão corretas, o item sempre é arrastado para cima.
     */
    public void sortAscending() {
        List<String> ascending = getItems().stream()
                .sorted(Comparator.comparingInt(this::valueOf))
                .collect(Collectors.toList());
        arrangeTo(ascending);
    }

    /** Reordena a lista, com drag and drop, para ficar exatamente na ordem informada. */
    public void arrangeTo(List<String> targetOrder) {
        removeAds();
        List<String> current = getItems();
        if (current.size() != targetOrder.size() || !current.containsAll(targetOrder)) {
            throw new IllegalArgumentException("A ordem desejada deve ter os mesmos itens da lista. Lista: "
                    + current + " | desejada: " + targetOrder);
        }
        for (int position = 0; position < targetOrder.size() - 1; position++) {
            moveToPosition(targetOrder.get(position), position);
        }
    }

    private void moveToPosition(String wanted, int position) {
        for (int attempt = 1; attempt <= MAX_DRAG_ATTEMPTS; attempt++) {
            List<WebElement> items = driver.findElements(LIST_ITEMS);
            if (textOf(items.get(position)).equals(wanted)) {
                return;
            }
            WebElement source = items.stream()
                    .filter(item -> textOf(item).equals(wanted))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Item não encontrado: " + wanted));

            dragAbove(source, items.get(position));

            try {
                new WebDriverWait(driver, REORDER_WAIT)
                        .until(d -> textOf(d.findElements(LIST_ITEMS).get(position)).equals(wanted));
                return;
            } catch (TimeoutException e) {
                // o arraste não pegou (a biblioteca de sortable é sensível ao movimento): tenta de novo
            }
        }
        throw new IllegalStateException("Não foi possível mover '" + wanted + "' para a posição "
                + position + " | ordem atual: " + getItems());
    }

    /**
     * Arraste feito com eventos de mouse separados (pressionar, mover, soltar), pois o sortable
     * do DemoQA só começa o arraste depois de um pequeno deslocamento e reage ao movimento.
     */
    private void dragAbove(WebElement source, WebElement target) {
        scrollTo(source);
        int offsetY = target.getSize().getHeight() / 4;
        new Actions(driver)
                .moveToElement(source)
                .clickAndHold()
                .pause(Duration.ofMillis(150))
                .moveByOffset(0, -10)
                .pause(Duration.ofMillis(150))
                .moveToElement(target, 0, -offsetY)
                .pause(Duration.ofMillis(250))
                .release()
                .perform();
    }

    private String textOf(WebElement element) {
        return element.getText().trim();
    }

    private int valueOf(String word) {
        Integer value = WORD_VALUE.get(word.toLowerCase());
        if (value == null) {
            throw new IllegalStateException("Item desconhecido para ordenação: " + word);
        }
        return value;
    }
}