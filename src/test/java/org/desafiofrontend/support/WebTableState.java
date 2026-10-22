package org.desafiofrontend.support;

import org.desafiofrontend.data.WebTableRecord;

import java.util.ArrayList;
import java.util.List;

/** Guarda, por cenário, os registros criados pela automação (injetado pelo PicoContainer). */
public class WebTableState {

    private final List<WebTableRecord> created = new ArrayList<>();
    private WebTableRecord current;
    private WebTableRecord previous;

    public void add(WebTableRecord record) {
        created.add(record);
        current = record;
    }

    /** Troca o registro atual pelo editado e guarda o antigo para validação. */
    public void replaceCurrent(WebTableRecord edited) {
        previous = current;
        int index = created.indexOf(current);
        created.set(index, edited);
        current = edited;
    }

    public void remove(WebTableRecord record) {
        created.remove(record);
    }

    public WebTableRecord getCurrent() {
        return current;
    }

    public WebTableRecord getPrevious() {
        return previous;
    }

    public List<WebTableRecord> getCreated() {
        return new ArrayList<>(created);
    }
}
