package org.desafiofrontend.data;

import java.util.List;

/** Um registro da tabela Web Tables (imutável). */
public record WebTableRecord(
        String firstName,
        String lastName,
        String email,
        int age,
        int salary,
        String department) {

    /** Valores na mesma ordem das colunas da tabela: First, Last, Age, Email, Salary, Department. */
    public List<String> toRowValues() {
        return List.of(firstName, lastName, String.valueOf(age), email, String.valueOf(salary), department);
    }
}
