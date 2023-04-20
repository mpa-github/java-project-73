package hexlet.code.domain.model;

public enum Status {

    NEW("Новый"),
    IN_PROGRESS("В работе"),
    ON_TESTING("На тестировании"),
    COMPLETED("Завершен");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
