package org.desafiofrontend.data;

import java.time.LocalDate;
import java.util.List;

/** Dados do estudante preenchidos no formulário (imutável). */
public record StudentData(
        String firstName,
        String lastName,
        String email,
        Gender gender,
        String mobile,
        LocalDate dateOfBirth,
        List<String> subjects,
        List<Hobby> hobbies,
        String address,
        String state,
        String city) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
