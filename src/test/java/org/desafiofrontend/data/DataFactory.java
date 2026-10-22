package org.desafiofrontend.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Gera massa de dados aleatória para o formulário. */
public final class DataFactory {

    private static final List<String> FIRST_NAMES = List.of("Fernando", "Maria", "Joao", "Ana", "Carlos", "Beatriz", "Lucas", "Julia");
    private static final List<String> LAST_NAMES = List.of("Souza", "Silva", "Oliveira", "Santos", "Lima", "Pereira", "Costa", "Ribeiro");
    private static final List<String> SUBJECTS = List.of("Maths", "Physics", "Chemistry", "English", "Biology", "Computer Science");

    /** Estado -> cidades válidas no combo do DemoQA. */
    private static final Map<String, List<String>> STATES_AND_CITIES = Map.of(
            "NCR", List.of("Delhi", "Gurgaon", "Noida"),
            "Uttar Pradesh", List.of("Agra", "Lucknow", "Merrut"),
            "Haryana", List.of("Karnal", "Panipat"),
            "Rajasthan", List.of("Jaipur", "Jaiselmer"));

    private DataFactory() {
    }

    public static StudentData randomStudent() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        String firstName = pick(FIRST_NAMES);
        String lastName = pick(LAST_NAMES);
        String email = (firstName + "." + lastName + rnd.nextInt(1000, 9999) + "@teste.com").toLowerCase();
        Gender gender = pick(List.of(Gender.values()));
        String mobile = "9" + String.format("%09d", rnd.nextLong(1_000_000_000L));
        LocalDate dob = LocalDate.of(rnd.nextInt(1970, 2006), rnd.nextInt(1, 13), rnd.nextInt(1, 29));

        List<String> subjects = new ArrayList<>(SUBJECTS);
        Collections.shuffle(subjects);
        subjects = new ArrayList<>(subjects.subList(0, rnd.nextInt(1, 3)));

        List<Hobby> hobbies = new ArrayList<>(List.of(Hobby.values()));
        Collections.shuffle(hobbies);
        hobbies = new ArrayList<>(hobbies.subList(0, rnd.nextInt(1, 4)));

        String state = pick(new ArrayList<>(STATES_AND_CITIES.keySet()));
        String city = pick(STATES_AND_CITIES.get(state));
        String address = "Rua Teste " + rnd.nextInt(1, 999) + ", Guarabira - PB";

        return new StudentData(firstName, lastName, email, gender, mobile, dob, subjects, hobbies, address, state, city);
    }

    private static <T> T pick(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }
}
