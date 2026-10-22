package org.desafiofrontend.data;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/** Gera registros aleatórios para a Web Tables. O e-mail é sempre único (usado para localizar a linha). */
public final class WebTableRecordFactory {

    private static final List<String> FIRST_NAMES = List.of("Fernando", "Maria", "Joao", "Ana", "Carlos", "Beatriz", "Lucas", "Julia", "Pedro", "Camila");
    private static final List<String> LAST_NAMES = List.of("Souza", "Silva", "Oliveira", "Santos", "Lima", "Pereira", "Costa", "Ribeiro", "Alves", "Rocha");
    private static final List<String> DEPARTMENTS = List.of("QA", "Dev", "Compliance", "Insurance", "Legal", "Finance", "Support");

    private WebTableRecordFactory() {
    }

    public static WebTableRecord random() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        String firstName = pick(FIRST_NAMES);
        String lastName = pick(LAST_NAMES);
        String unique = UUID.randomUUID().toString().substring(0, 8);
        String email = (firstName + "." + lastName + "." + unique + "@teste.com").toLowerCase();

        return new WebTableRecord(
                firstName,
                lastName,
                email,
                rnd.nextInt(18, 66),
                rnd.nextInt(1000, 20001),
                pick(DEPARTMENTS));
    }

    private static String pick(List<String> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
